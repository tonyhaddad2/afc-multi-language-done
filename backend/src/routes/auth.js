import express from 'express';
import jwt from 'jsonwebtoken';
import admin from 'firebase-admin';
import { z } from 'zod';
import { config } from '../config.js';
import { query } from '../db.js';
import { encryptField, encryptJson } from '../crypto.js';
import { normalizePhone, phoneHash } from '../identity.js';

export const authRouter = express.Router();

let initialized = false;
function initFirebase() {
  if (initialized) return true;
  if (!config.fcmServiceAccountJson) return false;
  try {
    const account = JSON.parse(config.fcmServiceAccountJson);
    if (!admin.apps.length) admin.initializeApp({ credential: admin.credential.cert(account) });
    initialized = true;
  } catch {
    initialized = false;
  }
  return initialized;
}

function token(userId,phone) {
  return jwt.sign(
    { sub: userId, phone },
    config.jwtSecret,
    { algorithm: 'HS256', expiresIn: config.jwtExpiresIn, issuer: 'be-lahza-backend', audience: 'be-lahza-mobile' }
  );
}

authRouter.post('/dev-token', async (req,res) => {
  if (config.env === 'production' && !config.allowDevAuth) return res.status(403).json({ error: 'dev_auth_disabled' });
  const body = z.object({ userId: z.string().min(2).max(120), phone: z.string().max(40).optional() }).parse(req.body);
  const id = body.userId.replace(/[^a-zA-Z0-9_@.+-]/g,'_');
  const phone = normalizePhone(body.phone || '');
  await query(
    `insert into users(user_id,phone_hash,encrypted_phone,encrypted_profile)
     values($1,$2,$3,$4)
     on conflict(user_id) do update set phone_hash=excluded.phone_hash,encrypted_phone=excluded.encrypted_phone,updated_at=now()`,
    [id,phone?phoneHash(phone):null,phone?encryptField(phone,`user:${id}:phone`):'',encryptJson({ devUser:true },`user:${id}:profile`)]
  );
  res.json({ ok:true,userId:id,token:token(id,phone),mode:'development_only' });
});

authRouter.post('/firebase', async (req,res) => {
  const body = z.object({ idToken: z.string().min(20).max(10000) }).parse(req.body);
  if (!initFirebase()) return res.status(503).json({ error:'firebase_not_configured' });
  const decoded = await admin.auth().verifyIdToken(body.idToken);
  const id = decoded.uid;
  const phone = normalizePhone(decoded.phone_number || '');
  await query(
    `insert into users(user_id,phone_hash,encrypted_phone,encrypted_profile)
     values($1,$2,$3,$4)
     on conflict(user_id) do update set phone_hash=excluded.phone_hash,encrypted_phone=excluded.encrypted_phone,updated_at=now()`,
    [id,phone?phoneHash(phone):null,phone?encryptField(phone,`user:${id}:phone`):'',encryptJson({ firebaseUser:true },`user:${id}:profile`)]
  );
  res.json({ ok:true,userId:id,token:token(id,phone) });
});
