import crypto from 'crypto';
import { config } from './config.js';

export function normalizePhone(value) {
  return String(value || '').trim().replace(/[^\d+]/g, '');
}

export function phoneHash(value) {
  const normalized = normalizePhone(value);
  if (!normalized) return '';
  return crypto.createHmac('sha256', config.phoneLookupSecret).update(normalized).digest('hex');
}
