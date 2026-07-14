import express from 'express';
import { z } from 'zod';
import { query } from '../db.js';
import { encryptField, encryptJson, decryptJson } from '../crypto.js';
import { normalizePhone, phoneHash } from '../identity.js';

export const usersRouter = express.Router();

usersRouter.put('/profile', async (req,res) => {
  const body = z.object({
    name: z.string().max(120).optional(),
    phone: z.string().max(40).optional(),
    language: z.enum(['en','fr','ar']).optional()
  }).parse(req.body);
  const phone = normalizePhone(body.phone || req.user.phone || '');
  const profile = { name: body.name || '', language: body.language || 'en' };
  await query(
    `insert into users(user_id,phone_hash,encrypted_phone,encrypted_profile,updated_at)
     values($1,$2,$3,$4,now())
     on conflict(user_id) do update set
       phone_hash=excluded.phone_hash,
       encrypted_phone=excluded.encrypted_phone,
       encrypted_profile=excluded.encrypted_profile,
       updated_at=now()`,
    [
      req.user.id,
      phone ? phoneHash(phone) : null,
      phone ? encryptField(phone,`user:${req.user.id}:phone`) : '',
      encryptJson(profile,`user:${req.user.id}:profile`)
    ]
  );
  res.json({ ok: true });
});

usersRouter.get('/profile', async (req,res) => {
  const result = await query('select encrypted_profile from users where user_id=$1',[req.user.id]);
  const encrypted = result.rows[0]?.encrypted_profile;
  res.json({ profile: encrypted ? decryptJson(encrypted,`user:${req.user.id}:profile`) : {} });
});

usersRouter.post('/device-token', async (req,res) => {
  const body = z.object({ token: z.string().min(10).max(4096), platform: z.string().max(30).default('android') }).parse(req.body);
  await query(
    `insert into users(user_id,phone_hash,encrypted_phone,encrypted_profile)
     values($1,$2,$3,$4) on conflict(user_id) do nothing`,
    [
      req.user.id,
      req.user.phone ? req.user.phoneHash : null,
      req.user.phone ? encryptField(req.user.phone,`user:${req.user.id}:phone`) : '',
      encryptJson({},`user:${req.user.id}:profile`)
    ]
  );
  await query(
    `insert into device_tokens(user_id,token,platform,updated_at)
     values($1,$2,$3,now())
     on conflict(token) do update set
       user_id=excluded.user_id,
       platform=excluded.platform,
       updated_at=now(),
       revoked_at=null`,
    [req.user.id,body.token,body.platform]
  );
  res.json({ ok: true });
});
