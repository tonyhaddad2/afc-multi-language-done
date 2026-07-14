import jwt from 'jsonwebtoken';
import { config } from './config.js';
import { normalizePhone, phoneHash } from './identity.js';

export function requireAuth(req, res, next) {
  const header = req.get('Authorization') || '';
  const token = header.startsWith('Bearer ') ? header.slice(7) : '';
  if (!token) return res.status(401).json({ error: 'missing_bearer_token' });
  try {
    const payload = jwt.verify(token, config.jwtSecret, { algorithms: ['HS256', 'RS256'] });
    const id = String(payload.sub || payload.user_id || payload.uid || '');
    if (!id) return res.status(401).json({ error: 'invalid_token_subject' });
    const phone = normalizePhone(payload.phone_number || payload.phone || '');
    req.user = { id, phone, phoneHash: phoneHash(phone) };
    next();
  } catch {
    res.status(401).json({ error: 'invalid_token' });
  }
}
