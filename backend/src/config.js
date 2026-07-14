import dotenv from 'dotenv';
dotenv.config();

function parseJson(value, fallback) {
  try { return value ? JSON.parse(value) : fallback; } catch { return fallback; }
}

export const config = Object.freeze({
  env: process.env.NODE_ENV || 'development',
  port: Number(process.env.PORT || 8080),
  databaseUrl: process.env.DATABASE_URL,
  jwtSecret: process.env.JWT_PUBLIC_KEY_OR_SECRET,
  jwtExpiresIn: process.env.JWT_EXPIRES_IN || '30m',
  encryptionKeyBase64: process.env.FIELD_ENCRYPTION_KEY_BASE64,
  keyId: process.env.FIELD_ENCRYPTION_KEY_ID || 'primary-v1',
  oldEncryptionKeys: parseJson(process.env.OLD_FIELD_ENCRYPTION_KEYS_JSON, []),
  phoneLookupSecret: process.env.PHONE_LOOKUP_HMAC_SECRET,
  fcmServiceAccountJson: process.env.FCM_SERVICE_ACCOUNT_JSON,
  allowedOrigins: (process.env.ALLOWED_ORIGINS || '').split(',').map(v => v.trim()).filter(Boolean),
  rateLimitWindowMs: Number(process.env.RATE_LIMIT_WINDOW_MS || 60000),
  rateLimitMax: Number(process.env.RATE_LIMIT_MAX || 120),
  allowDevAuth: process.env.ALLOW_DEV_AUTH === 'true',
  adminUserIds: (process.env.ADMIN_USER_IDS || '').split(',').map(v => v.trim()).filter(Boolean)
});

export function requireConfig() {
  const missing = [];
  if (!config.databaseUrl) missing.push('DATABASE_URL');
  if (!config.jwtSecret) missing.push('JWT_PUBLIC_KEY_OR_SECRET');
  if (!config.encryptionKeyBase64) missing.push('FIELD_ENCRYPTION_KEY_BASE64');
  if (!config.phoneLookupSecret) missing.push('PHONE_LOOKUP_HMAC_SECRET');
  if (missing.length) throw new Error(`Missing backend configuration: ${missing.join(', ')}`);
}
