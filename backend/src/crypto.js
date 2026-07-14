
import crypto from 'crypto';
import { config } from './config.js';
import { currentKey, keyForId } from './keyring.js';

const VERSION = 'v2';
const LEGACY_VERSION = 'v1';

export function encryptField(value, aad = '') {
  const plain = value == null ? '' : String(value);
  const { keyId, key } = currentKey();
  const iv = crypto.randomBytes(12);
  const cipher = crypto.createCipheriv('aes-256-gcm', key, iv);
  const authData = `${keyId}:${aad}`;
  if (authData) cipher.setAAD(Buffer.from(authData));
  const ciphertext = Buffer.concat([cipher.update(plain, 'utf8'), cipher.final()]);
  const tag = cipher.getAuthTag();
  return `${VERSION}:${keyId}:${iv.toString('base64')}:${tag.toString('base64')}:${ciphertext.toString('base64')}`;
}

export function decryptField(packed, aad = '') {
  if (!packed) return '';
  const parts = String(packed).split(':');

  if (parts[0] === LEGACY_VERSION && parts.length === 4) {
    const [, ivB64, tagB64, cipherB64] = parts;
    const key = currentKey().key;
    const decipher = crypto.createDecipheriv('aes-256-gcm', key, Buffer.from(ivB64, 'base64'));
    if (aad) decipher.setAAD(Buffer.from(aad));
    decipher.setAuthTag(Buffer.from(tagB64, 'base64'));
    const plain = Buffer.concat([decipher.update(Buffer.from(cipherB64, 'base64')), decipher.final()]);
    return plain.toString('utf8');
  }

  if (parts[0] !== VERSION || parts.length !== 5) {
    throw new Error('Unsupported encrypted field format');
  }
  const [, keyId, ivB64, tagB64, cipherB64] = parts;
  const key = keyForId(keyId);
  const decipher = crypto.createDecipheriv('aes-256-gcm', key, Buffer.from(ivB64, 'base64'));
  const authData = `${keyId}:${aad}`;
  if (authData) decipher.setAAD(Buffer.from(authData));
  decipher.setAuthTag(Buffer.from(tagB64, 'base64'));
  const plain = Buffer.concat([decipher.update(Buffer.from(cipherB64, 'base64')), decipher.final()]);
  return plain.toString('utf8');
}

export function encryptJson(value, aad = '') {
  return encryptField(JSON.stringify(value ?? {}), aad);
}

export function decryptJson(packed, aad = '') {
  const raw = decryptField(packed, aad);
  return raw ? JSON.parse(raw) : {};
}
