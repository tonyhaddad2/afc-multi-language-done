import express from 'express';
import { z } from 'zod';
import { query, withTransaction } from '../db.js';
import { encryptField, decryptField, decryptJson } from '../crypto.js';
import { normalizePhone, phoneHash } from '../identity.js';
import { sendTrustedInvitePush } from '../push.js';

export const trustedContactsRouter = express.Router();

trustedContactsRouter.post('/invite', async (req,res) => {
  const body = z.object({ phone: z.string().min(4).max(40) }).parse(req.body);
  const phone = normalizePhone(body.phone);
  const hash = phoneHash(phone);
  if (!hash || hash === req.user.phoneHash) return res.status(400).json({ error:'invalid_contact' });

  const existing = await query(
    `select invite_id from trusted_contact_invites
     where owner_user_id=$1 and invited_phone_hash=$2 and status='pending'
     order by created_at desc limit 1`,
    [req.user.id,hash]
  );
  if (existing.rows.length) return res.json({ ok:true,inviteId:existing.rows[0].invite_id,duplicate:true });

  const target = await query('select user_id from users where phone_hash=$1 and disabled_at is null',[hash]);
  const invite = await query(
    `insert into trusted_contact_invites(owner_user_id,invited_phone_hash,encrypted_invited_phone,status)
     values($1,$2,$3,'pending') returning invite_id`,
    [req.user.id,hash,encryptField(phone,`invite:${req.user.id}:${hash}:phone`)]
  );
  if (target.rows.length) await sendTrustedInvitePush(target.rows[0].user_id);
  res.json({ ok:true,inviteId:invite.rows[0].invite_id,userRegistered:target.rows.length>0 });
});

trustedContactsRouter.get('/invites', async (req,res) => {
  if (!req.user.phoneHash) return res.json({ invites:[] });
  const result = await query(
    `select i.invite_id,i.owner_user_id,u.encrypted_profile,u.encrypted_phone
     from trusted_contact_invites i
     join users u on u.user_id=i.owner_user_id
     where i.invited_phone_hash=$1 and i.status='pending'
     order by i.created_at desc`,
    [req.user.phoneHash]
  );
  const invites = result.rows.map(row => {
    const profile = row.encrypted_profile ? decryptJson(row.encrypted_profile,`user:${row.owner_user_id}:profile`) : {};
    const phone = row.encrypted_phone ? decryptField(row.encrypted_phone,`user:${row.owner_user_id}:phone`) : '';
    return { inviteId:row.invite_id,ownerUserId:row.owner_user_id,ownerName:profile.name||'be lahza user',ownerPhone:phone };
  });
  res.json({ invites });
});

trustedContactsRouter.post('/accept', async (req,res) => {
  const body = z.object({ inviteId: z.string().uuid() }).parse(req.body);
  const invite = await query(
    `select * from trusted_contact_invites
     where invite_id=$1 and invited_phone_hash=$2 and status='pending'`,
    [body.inviteId,req.user.phoneHash]
  );
  if (!invite.rows.length) return res.status(404).json({ error:'invite_not_found' });

  await withTransaction(async client => {
    const owner = invite.rows[0].owner_user_id;
    await client.query(
      `update trusted_contact_invites set status='accepted',accepted_user_id=$1,accepted_at=now() where invite_id=$2`,
      [req.user.id,body.inviteId]
    );
    await client.query(
      `insert into trusted_contacts(owner_user_id,contact_user_id,status)
       values($1,$2,'accepted')
       on conflict(owner_user_id,contact_user_id) do update set status='accepted',updated_at=now()`,
      [owner,req.user.id]
    );
  });
  res.json({ ok:true });
});

trustedContactsRouter.post('/reject', async (req,res) => {
  const body = z.object({ inviteId: z.string().uuid() }).parse(req.body);
  await query(
    `update trusted_contact_invites set status='rejected',rejected_at=now()
     where invite_id=$1 and invited_phone_hash=$2 and status='pending'`,
    [body.inviteId,req.user.phoneHash]
  );
  res.json({ ok:true });
});

trustedContactsRouter.get('/', async (req,res) => {
  const result = await query(
    `select tc.contact_user_id,tc.priority,u.encrypted_profile,u.encrypted_phone
     from trusted_contacts tc
     join users u on u.user_id=tc.contact_user_id
     where tc.owner_user_id=$1 and tc.status='accepted'
     order by tc.priority asc,tc.created_at asc`,
    [req.user.id]
  );
  const contacts = result.rows.map(row => {
    const profile = row.encrypted_profile ? decryptJson(row.encrypted_profile,`user:${row.contact_user_id}:profile`) : {};
    const phone = row.encrypted_phone ? decryptField(row.encrypted_phone,`user:${row.contact_user_id}:phone`) : '';
    return { contactUserId:row.contact_user_id,name:profile.name||'Trusted contact',phone,priority:row.priority };
  });
  res.json({ contacts });
});

trustedContactsRouter.post('/remove', async (req,res) => {
  const body = z.object({ contactUserId: z.string().min(1).max(200) }).parse(req.body);
  await query(
    `update trusted_contacts set status='removed',updated_at=now()
     where owner_user_id=$1 and contact_user_id=$2`,
    [req.user.id,body.contactUserId]
  );
  res.json({ ok:true });
});
