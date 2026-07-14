import express from 'express';
import { z } from 'zod';
import { query, withTransaction } from '../db.js';
import { encryptField, encryptJson, decryptField, decryptJson } from '../crypto.js';
import { sendEmergencyPush } from '../push.js';
import { audit } from '../audit.js';

export const emergenciesRouter = express.Router();

const createSchema = z.object({
  clientRequestId: z.string().min(8).max(200),
  emergencyType: z.string().min(1).max(120),
  note: z.string().max(3000).optional(),
  location: z.string().max(1200).optional(),
  medicalIdText: z.string().max(7000).optional()
});

emergenciesRouter.get('/', async (req,res) => {
  const result = await query(
    `select emergency_id,client_request_id,emergency_type,status,started_at,ended_at,last_location_at,trusted_contacts_notified
     from emergency_sessions where user_id=$1 order by started_at desc limit 50`,
    [req.user.id]
  );
  res.json({ emergencies:result.rows });
});

emergenciesRouter.get('/active', async (req,res) => {
  const result = await query(
    `select emergency_id,client_request_id,emergency_type,status,started_at,last_location_at,trusted_contacts_notified
     from emergency_sessions where user_id=$1 and status='active' order by started_at desc limit 1`,
    [req.user.id]
  );
  res.json({ emergency:result.rows[0] || null });
});

emergenciesRouter.post('/', async (req,res) => {
  const body = createSchema.parse(req.body);

  const duplicate = await query(
    `select emergency_id,trusted_contacts_notified,status
     from emergency_sessions where user_id=$1 and client_request_id=$2`,
    [req.user.id,body.clientRequestId]
  );
  if (duplicate.rows.length) {
    const row = duplicate.rows[0];
    return res.json({ ok:true,emergencyId:row.emergency_id,notified:row.trusted_contacts_notified,status:row.status,idempotent:true });
  }

  const result = await withTransaction(async client => {
    const med = await client.query(
      'select encrypted_medical_id,visible_fields from medical_ids where user_id=$1',
      [req.user.id]
    );
    const visible = med.rows[0]?.visible_fields ||
      ['fullName','bloodType','allergies','conditions','medications','emergencyContacts'];
    const snapshot = med.rows[0]?.encrypted_medical_id ||
      encryptJson({ notes:body.medicalIdText || '' },`user:${req.user.id}:medical`);

    const inserted = await client.query(
      `insert into emergency_sessions(
         user_id,client_request_id,emergency_type,encrypted_note,encrypted_initial_location,
         encrypted_medical_snapshot,visible_fields,status
       ) values($1,$2,$3,$4,$5,$6,$7,'active')
       returning emergency_id`,
      [
        req.user.id,
        body.clientRequestId,
        body.emergencyType,
        encryptField(body.note || '',`emergency:${body.clientRequestId}:note`),
        encryptField(body.location || '',`emergency:${body.clientRequestId}:initial_location`),
        snapshot,
        visible
      ]
    );

    const emergencyId = inserted.rows[0].emergency_id;
    const contacts = await client.query(
      `select contact_user_id from trusted_contacts
       where owner_user_id=$1 and status='accepted'`,
      [req.user.id]
    );

    for (const contact of contacts.rows) {
      await client.query(
        `insert into emergency_notifications(emergency_id,recipient_user_id,type)
         values($1,$2,'emergency_triggered')`,
        [emergencyId,contact.contact_user_id]
      );
    }

    await client.query(
      'update emergency_sessions set trusted_contacts_notified=$1 where emergency_id=$2',
      [contacts.rows.length,emergencyId]
    );

    return { emergencyId,contactIds:contacts.rows.map(row => row.contact_user_id) };
  });

  await audit(req.user.id,'create_emergency','emergency',result.emergencyId,'idempotent_session_created');
  const push = await sendEmergencyPush(result.contactIds,{ emergencyId:result.emergencyId });
  res.json({ ok:true,emergencyId:result.emergencyId,notified:result.contactIds.length,push });
});

emergenciesRouter.post('/:id/location', async (req,res) => {
  const body = z.object({
    lat:z.number().min(-90).max(90),
    lng:z.number().min(-180).max(180),
    accuracy:z.number().min(0).max(10000).optional(),
    battery:z.number().min(0).max(100).optional(),
    source:z.string().max(30).optional()
  }).parse(req.body);

  const owner = await query(
    `select user_id from emergency_sessions
     where emergency_id=$1 and status='active'`,
    [req.params.id]
  );
  if (!owner.rows.length || owner.rows[0].user_id !== req.user.id) return res.status(403).json({ error:'not_allowed' });

  await query(
    `insert into emergency_locations(emergency_id,user_id,encrypted_location,accuracy,battery_level,source)
     values($1,$2,$3,$4,$5,$6)`,
    [
      req.params.id,
      req.user.id,
      encryptJson({ lat:body.lat,lng:body.lng },`emergency:${req.params.id}:location`),
      body.accuracy ?? null,
      body.battery ?? null,
      body.source || 'gps'
    ]
  );
  await query('update emergency_sessions set last_location_at=now() where emergency_id=$1',[req.params.id]);
  res.json({ ok:true });
});

emergenciesRouter.get('/:id', async (req,res) => {
  const result = await query(
    `select e.*,
            case when e.user_id=$2 then true else false end as is_owner
     from emergency_sessions e
     left join trusted_contacts tc
       on tc.owner_user_id=e.user_id
      and tc.contact_user_id=$2
      and tc.status='accepted'
     where e.emergency_id=$1 and (e.user_id=$2 or tc.contact_user_id=$2)`,
    [req.params.id,req.user.id]
  );
  if (!result.rows.length) return res.status(404).json({ error:'not_found' });
  const session = result.rows[0];

  await audit(req.user.id,'decrypt_emergency_snapshot','emergency',req.params.id,'authorized_emergency_view');
  const fullMedical = decryptJson(session.encrypted_medical_snapshot,`user:${session.user_id}:medical`);
  const medicalId = filterMedical(fullMedical,session.visible_fields || []);

  const profileResult = await query('select encrypted_profile from users where user_id=$1',[session.user_id]);
  const ownerProfile = profileResult.rows[0]?.encrypted_profile
    ? decryptJson(profileResult.rows[0].encrypted_profile,`user:${session.user_id}:profile`)
    : {};

  const locationResult = await query(
    `select encrypted_location,accuracy,battery_level,created_at
     from emergency_locations where emergency_id=$1
     order by created_at desc limit 1`,
    [req.params.id]
  );
  let latestLocation = null;
  if (locationResult.rows.length) {
    await audit(req.user.id,'decrypt_emergency_location','emergency',req.params.id,'authorized_emergency_view');
    latestLocation = {
      ...decryptJson(locationResult.rows[0].encrypted_location,`emergency:${req.params.id}:location`),
      accuracy:locationResult.rows[0].accuracy,
      battery:locationResult.rows[0].battery_level,
      createdAt:locationResult.rows[0].created_at
    };
  }

  const updatesResult = await query(
    `select actor_user_id,status,encrypted_note,created_at
     from emergency_status_updates where emergency_id=$1
     order by created_at desc limit 30`,
    [req.params.id]
  );
  const statusUpdates = updatesResult.rows.map(row => ({
    actorUserId:row.actor_user_id,
    status:row.status,
    note:row.encrypted_note ? decryptField(row.encrypted_note,`emergency:${req.params.id}:status`) : '',
    createdAt:row.created_at
  }));

  res.json({
    emergencyId:session.emergency_id,
    emergencyType:session.emergency_type,
    status:session.status,
    ownerProfile,
    medicalId,
    latestLocation,
    statusUpdates,
    startedAt:session.started_at,
    endedAt:session.ended_at
  });
});

emergenciesRouter.post('/:id/status', async (req,res) => {
  const body = z.object({
    status:z.enum(['seen','helping','cannot_help','comment']),
    note:z.string().max(1000).optional()
  }).parse(req.body);

  const allowed = await query(
    `select e.user_id from emergency_sessions e
     join trusted_contacts tc
       on tc.owner_user_id=e.user_id
      and tc.contact_user_id=$2
      and tc.status='accepted'
     where e.emergency_id=$1`,
    [req.params.id,req.user.id]
  );
  if (!allowed.rows.length) return res.status(403).json({ error:'not_allowed' });

  await query(
    `insert into emergency_status_updates(emergency_id,actor_user_id,status,encrypted_note)
     values($1,$2,$3,$4)`,
    [req.params.id,req.user.id,body.status,encryptField(body.note || '',`emergency:${req.params.id}:status`)]
  );
  res.json({ ok:true });
});

emergenciesRouter.post('/:id/resolve', async (req,res) => {
  const updated = await query(
    `update emergency_sessions set status='resolved',ended_at=now()
     where emergency_id=$1 and user_id=$2 and status='active'
     returning emergency_id`,
    [req.params.id,req.user.id]
  );
  if (!updated.rows.length) return res.status(404).json({ error:'not_found' });
  const contacts = await query(
    `select contact_user_id from trusted_contacts where owner_user_id=$1 and status='accepted'`,
    [req.user.id]
  );
  await sendEmergencyPush(contacts.rows.map(row => row.contact_user_id),{ emergencyId:req.params.id,status:'resolved' });
  await audit(req.user.id,'resolve_emergency','emergency',req.params.id,'user_marked_safe');
  res.json({ ok:true });
});

emergenciesRouter.post('/:id/cancel', async (req,res) => {
  const updated = await query(
    `update emergency_sessions set status='cancelled',ended_at=now()
     where emergency_id=$1 and user_id=$2 and status='active'
     returning emergency_id`,
    [req.params.id,req.user.id]
  );
  if (!updated.rows.length) return res.status(404).json({ error:'not_found' });
  const contacts = await query(
    `select contact_user_id from trusted_contacts where owner_user_id=$1 and status='accepted'`,
    [req.user.id]
  );
  await sendEmergencyPush(contacts.rows.map(row => row.contact_user_id),{ emergencyId:req.params.id,status:'cancelled' });
  await audit(req.user.id,'cancel_emergency','emergency',req.params.id,'false_alarm');
  res.json({ ok:true });
});

function filterMedical(medical,visibleFields) {
  const allowed = new Set(visibleFields || []);
  const out = {};
  for (const field of ['fullName','bloodType','allergies','conditions','medications','notes','emergencyContacts']) {
    if (allowed.has(field) && medical[field] != null) out[field] = medical[field];
  }
  return out;
}
