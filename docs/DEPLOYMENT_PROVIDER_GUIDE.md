# Deployment Provider Guide

## Option A: Render

Use root `render.yaml`.

1. Create Render account.
2. Create PostgreSQL database.
3. Create web service from repository.
4. Set environment variables from backend `.env.example`.
5. Run migration command once:

```bash
cd backend
npm install
npm run migrate
```

## Option B: Railway

Use root `railway.json`.

1. Create Railway project.
2. Add PostgreSQL plugin.
3. Deploy repository.
4. Set env vars.
5. Run `npm run migrate`.

## Option C: Fly.io

Use root `fly.toml`.

1. Run `fly launch`.
2. Set secrets:

```bash
fly secrets set DATABASE_URL=...
fly secrets set JWT_PUBLIC_KEY_OR_SECRET=...
fly secrets set FIELD_ENCRYPTION_KEY_BASE64=...
fly secrets set FCM_SERVICE_ACCOUNT_JSON=...
```

3. Deploy:

```bash
fly deploy
```

## Health check

```text
GET /health
```

Expected:

```json
{"ok":true,"service":"be-lahza-backend","version":"0.8.3"}
```
