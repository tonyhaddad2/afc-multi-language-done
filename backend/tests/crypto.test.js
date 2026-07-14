import test from 'node:test';
import assert from 'node:assert/strict';

process.env.DATABASE_URL = process.env.DATABASE_URL || 'postgres://local/local';
process.env.JWT_PUBLIC_KEY_OR_SECRET = process.env.JWT_PUBLIC_KEY_OR_SECRET || 'test-secret';
process.env.FIELD_ENCRYPTION_KEY_BASE64 = process.env.FIELD_ENCRYPTION_KEY_BASE64 || Buffer.from('0123456789abcdef0123456789abcdef').toString('base64');
process.env.FIELD_ENCRYPTION_KEY_ID = 'test-key';

const cryptoMod = await import('../src/crypto.js');

test('encrypt/decrypt field round trip with AAD', () => {
  const packed = cryptoMod.encryptField('sensitive medical data', 'user:test:medical');
  assert.notEqual(packed.includes('sensitive medical data'), true);
  assert.equal(cryptoMod.decryptField(packed, 'user:test:medical'), 'sensitive medical data');
});

test('encrypt/decrypt json round trip', () => {
  const packed = cryptoMod.encryptJson({ bloodType: 'O+', allergies: 'Penicillin' }, 'user:test:medical');
  const out = cryptoMod.decryptJson(packed, 'user:test:medical');
  assert.equal(out.bloodType, 'O+');
  assert.equal(out.allergies, 'Penicillin');
});
