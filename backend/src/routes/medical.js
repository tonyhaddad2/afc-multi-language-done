import express from 'express';
import { z } from 'zod';
import { query } from '../db.js';
import { encryptJson, decryptJson } from '../crypto.js';
import { audit } from '../audit.js';

export const medicalRouter = express.Router();

const allowedFields = ['fullName','bloodType','allergies','conditions','medications','notes','emergencyContacts'];

const schema = z.object({
  fullName: z.string().max(140).optional(),
  bloodType: z.string().max(20).optional(),
  allergies: z.string().max(2000).optional(),
  conditions: z.string().max(2000).optional(),
  medications: z.string().max(2000).optional(),
  notes: z.string().max(3000).optional(),
  emergencyContacts: z.array(z.object({
    name: z.string().max(120).optional(),
    phone: z.string().max(40)
  })).max(5).optional(),
  visibleFields: z.array(z.enum(allowedFields)).max(allowedFields.length).optional()
});

medicalRouter.put('/', async (req,res) => {
  const body = schema.parse(req.body);
  const visible = body.visibleFields || ['fullName','bloodType','allergies','conditions','medications','emergencyContacts'];
  const medical = { ...body };
  delete medical.visibleFields;
  await query(
    `insert into medical_ids(user_id,encrypted_medical_id,visible_fields,updated_at)
     values($1,$2,$3,now())
     on conflict(user_id) do update set
       encrypted_medical_id=excluded.encrypted_medical_id,
       visible_fields=excluded.visible_fields,
       updated_at=now()`,
    [req.user.id,encryptJson(medical,`user:${req.user.id}:medical`),visible]
  );
  res.json({ ok: true });
});

medicalRouter.get('/', async (req,res) => {
  const result = await query('select encrypted_medical_id,visible_fields from medical_ids where user_id=$1',[req.user.id]);
  if (!result.rows.length) return res.json({ medicalId: {}, visibleFields: [] });
  await audit(req.user.id,'decrypt_own_medical_id','medical_id',req.user.id,'owner_view');
  res.json({
    medicalId: decryptJson(result.rows[0].encrypted_medical_id,`user:${req.user.id}:medical`),
    visibleFields: result.rows[0].visible_fields
  });
});
