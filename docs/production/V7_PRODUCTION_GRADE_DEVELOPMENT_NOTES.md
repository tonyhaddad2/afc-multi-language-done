# be lahza V7 production-grade development notes

This package upgrades the prototype into a production-foundation codebase.

## Implemented in Android project

- AI Coach remains camera-only.
- Rescuer auto-lock remains inside AI Coach.
- Local Medical ID is encrypted with Android Keystore AES-GCM.
- Emergency trigger starts an active emergency mode.
- Emergency Medical ID notification is created for lock-screen visibility.
- EmergencyMedicalIdActivity opens over lock screen when supported.
- Connected trusted-contact screen exists for backend invite integration.
- Android app includes backend API client hooks.
- Cleartext traffic disabled in manifest.
- Additional production permissions added: notifications, foreground service, wake lock.

## Implemented in backend folder

- Node/Express API skeleton.
- PostgreSQL schema.
- AES-256-GCM server-side field encryption/decryption.
- Encrypted Medical ID storage.
- Encrypted emergency note/location storage.
- Emergency Medical ID snapshot handling.
- Trusted contact invites.
- Emergency sessions.
- Live location endpoint foundation.
- FCM push notification module foundation.
- Docker Compose for local database.

## Important boundary

This is a development foundation, not a live production deployment. To become live, configure:

- Firebase/Auth or another real identity provider.
- Real backend domain.
- JWT verification keys.
- FCM service account.
- 32-byte KMS-managed encryption key.
- PostgreSQL production database.
- Monitoring and deployment pipeline.

## Encryption requirement fulfilled in architecture

All sensitive app data must be encrypted in transit and at rest. The backend encrypts sensitive fields before database storage and decrypts only inside authorized routes. Medical ID snapshots, notes, and emergency location are stored encrypted.

## Next developer task

Connect Firebase Auth in the Android app and set `auth_bearer_token` from the Firebase ID token before calling backend APIs.
