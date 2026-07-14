import crypto from 'crypto';

const oldKeyId = process.env.FIELD_ENCRYPTION_KEY_ID || 'primary-v1';
const oldKey = process.env.FIELD_ENCRYPTION_KEY_BASE64 || '';
const newKeyId = `primary-${new Date().toISOString().slice(0, 10).replaceAll('-', '')}`;
const newKey = crypto.randomBytes(32).toString('base64');

console.log('# New active key');
console.log(`FIELD_ENCRYPTION_KEY_ID=${newKeyId}`);
console.log(`FIELD_ENCRYPTION_KEY_BASE64=${newKey}`);
console.log('');
console.log('# Keep old keys available for decrypt during rotation');
const oldKeys = oldKey ? [{ keyId: oldKeyId, keyBase64: oldKey }] : [];
console.log(`OLD_FIELD_ENCRYPTION_KEYS_JSON='${JSON.stringify(oldKeys)}'`);
console.log('');
console.log('# Rotation rule: new writes use new key; old records decrypt with old keys until re-encrypted.');
