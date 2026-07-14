# be lahza backend — production foundation

This backend is the development foundation for the connected emergency version.

It includes:

- authenticated API structure
- encrypted Medical ID storage
- encrypted emergency notes/location storage
- trusted-contact invite tables
- active emergency sessions
- Medical ID emergency snapshot storage
- FCM push notification module
- PostgreSQL schema
- Docker Compose for local Postgres

## Encryption model

Sensitive fields are encrypted server-side before database storage using AES-256-GCM.

Network path:

```text
Android app -> HTTPS/TLS -> Backend -> encrypt/decrypt in controlled routes -> PostgreSQL encrypted columns
```

The database never receives plaintext Medical ID, emergency notes, or location fields.

## Local start

```bash
cp .env.example .env
# set FIELD_ENCRYPTION_KEY_BASE64 to a 32-byte base64 key
npm install
npm run check
docker compose up -d
npm run dev
```

## Generate encryption key

```bash
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

## Important

This is a production architecture foundation. It still needs real Firebase/Auth credentials, domain, monitoring, CI secrets, and deployment infrastructure before it is live.
