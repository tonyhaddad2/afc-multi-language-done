import { config } from './config.js';

function decodeKey(keyBase64, label) {
  const key = Buffer.from(keyBase64 || '', 'base64');
  if (key.length !== 32) {
    throw new Error(`${label} must decode to exactly 32 bytes for AES-256-GCM`);
  }
  return key;
}

export function currentKey() {
  return {
    keyId: config.keyId,
    key: decodeKey(config.encryptionKeyBase64, 'FIELD_ENCRYPTION_KEY_BASE64')
  };
}

export function keyForId(keyId) {
  const current = currentKey();
  if (keyId === current.keyId) return current.key;
  for (const old of config.oldEncryptionKeys) {
    if (old.keyId === keyId) return decodeKey(old.keyBase64, `OLD_FIELD_ENCRYPTION_KEYS_JSON.${keyId}`);
  }
  throw new Error(`Unknown encryption key id: ${keyId}`);
}
