import crypto from 'crypto';

const jwtSecret = crypto.randomBytes(48).toString('base64url');
const fieldKey = crypto.randomBytes(32).toString('base64');
const keyId = `primary-${new Date().toISOString().slice(0, 10).replaceAll('-', '')}`;

console.log('# Paste these into your hosting provider secrets/environment variables.');
console.log(`JWT_PUBLIC_KEY_OR_SECRET=${jwtSecret}`);
console.log(`FIELD_ENCRYPTION_KEY_BASE64=${fieldKey}`);
console.log(`FIELD_ENCRYPTION_KEY_ID=${keyId}`);
console.log('JWT_EXPIRES_IN=30m');
console.log('ALLOW_DEV_AUTH=false');

console.log(`PHONE_LOOKUP_HMAC_SECRET=${crypto.randomBytes(48).toString('base64url')}`);
