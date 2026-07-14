# Deployment Guide

## 1. Backend

Deploy the backend folder to a Node.js host such as Render, Fly.io, Railway, AWS, GCP, or a VPS.

Required environment variables:

```bash
DATABASE_URL=postgres://...
JWT_PUBLIC_KEY_OR_SECRET=...
FIELD_ENCRYPTION_KEY_BASE64=...
FIELD_ENCRYPTION_KEY_ID=primary-v1
JWT_EXPIRES_IN=30m
ADMIN_USER_IDS=your-admin-user-id
ALLOW_DEV_AUTH=false
FCM_SERVICE_ACCOUNT_JSON={...}
ALLOWED_ORIGINS=https://your-admin-dashboard.example
```

Generate `FIELD_ENCRYPTION_KEY_BASE64`:

```bash
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"
```

Run database migration:

```bash
psql "$DATABASE_URL" -f backend/migrations/0001_initial.sql
```

Start server:

```bash
cd backend
npm install
npm run check
npm test
npm start
```

## 2. Android

Set the production API URL in:

```text
app/src/main/res/values/strings.xml
```

or inside the app developer connection screen while testing.

Build debug APK through GitHub Actions:

```text
Actions -> Build be lahza Android APK -> Run workflow
```

Production release still needs a release keystore and Play Store AAB workflow.

## 3. End-to-end test

1. Create user A token.
2. Create user B token.
3. User A sends trusted-contact invite.
4. User B accepts.
5. User A triggers emergency.
6. User B opens trusted emergency screen with emergency ID.
7. User B marks seen/helping.
8. User A resolves emergency.
