# be lahza V9.0 — Professional Reliability Release

Android emergency-support application for Lebanon with multilingual emergency guides, cancelable SOS activation, trusted-contact alerts, live location, encrypted Medical ID, CPR posture coaching, and deployable backend foundations.

## Build debug APK

```text
Actions -> Build be lahza Android APK -> Run workflow
```

Artifact:

```text
be-lahza-v90-professional-reliability-debug-apk
```

Inside:

```text
app-debug.apk
```

## Release build

Use the release AAB workflow after configuring the private signing secrets described in `docs/RELEASE_SIGNING_AAB.md`.

## Essential deployment setup

See:

- `docs/V90_PROFESSIONAL_RELIABILITY_RELEASE.md`
- `docs/V90_REMAINING_EXTERNAL_CONFIGURATION.md`
- `docs/FIREBASE_SETUP.md`
- `docs/DEPLOYMENT_PROVIDER_GUIDE.md`
- `docs/TESTING_CHECKLIST.md`


## Previous development history

# be lahza V8.0 — Connected Emergency MVP Foundation

This package upgrades the previous production foundation into a fuller connected-emergency development build.

## Included now

- Android emergency app
- Camera-only AI Coach with rescuer auto-lock
- Lock-screen Medical ID notification and emergency Medical ID screen
- Active emergency screen
- Foreground live-location sharing service
- Production connection/login developer screen
- Trusted contacts invite/accept screens/hooks
- Trusted contact emergency receiver screen
- Settings screen for backend URL and bearer token
- Encrypted local storage using Android Keystore AES-GCM
- Backend Node.js API foundation
- PostgreSQL schema for users, Medical ID, trusted contacts, emergency sessions, location updates, audit logs, app config, and notifications
- Server-side AES-256-GCM field encryption/decryption
- Push notification foundation through FCM
- Production-style mannequin MP4 animation assets in `app/src/main/res/raw/`

## Build artifact

GitHub Actions creates:

`be-lahza-v80-connected-emergency-mvp-debug-apk`

Inside the artifact:

`app-debug.apk`

## Critical production configuration still needed

This package contains the app/backend code foundation. To make it live, deploy the backend, configure a real domain, create production database, configure JWT/Firebase auth, configure FCM credentials, set the backend encryption key, and replace bearer-token testing with real phone/email authentication.


## V8.1 professional animation pass

This package includes a full replacement of the raw guide MP4 assets with a consistent 3D mannequin-style emergency-training animation set.

The AI Coach remains camera-only and does not cover the camera with videos. The animation clips play in the guide/lesson screens.

GitHub Actions artifact:

`be-lahza-v81-professional-3d-animations-debug-apk`


## V8.2 Connected Emergency Hardening

This package adds the next production-development pass:

- Android connected emergency hardening screens
- real backend API hooks in the Android app
- non-production dev-token flow
- Firebase ID-token exchange backend foundation
- encrypted offline sync queue
- offline fallback screen
- Medical ID privacy/visible-fields screen
- permission setup screen
- trusted-contact emergency receiver response buttons
- emergency history screen
- remote config screen
- operations dashboard hooks
- backend admin metrics/config endpoints
- backend AES-256-GCM v2 field encryption with key id
- backend crypto tests and notification safety tests
- deployment guide, test checklist, and animation studio pipeline docs

AI Coach remains camera-only with rescuer auto-lock. Animations play in guide/lesson screens, not over the AI camera.

GitHub artifact:

`be-lahza-v82-connected-hardening-debug-apk`


## V8.3 Deployment + Firebase + Release Readiness

This version adds the deployment and production-readiness pieces that can be done inside the repository:

- Firebase Auth/Messaging dependencies
- conditional `google-services.json` support
- Firebase phone login screen
- Firebase Messaging Service
- FCM token registration
- two-phone production test screen
- signed release AAB workflow
- backend Dockerfile
- Render/Railway/Fly deployment configs
- migration runner
- backup/restore scripts
- secret/key generation scripts
- key rotation helper
- KMS/keyring-ready encryption
- Blender production pipeline scripts and storyboards

GitHub debug artifact:

`be-lahza-v83-deployment-firebase-release-debug-apk`

GitHub release artifact:

`be-lahza-v83-release-aab-apk`


## V8.4 Multilingual + Smart Search

This version adds:

- English / French / Arabic language option
- language selector screen
- localized main dashboard
- localized emergency cards
- localized guide labels
- localized emergency numbers labels
- stronger typo-tolerant smart search
- synonym search across English, French, and Arabic

GitHub debug artifact:

`be-lahza-v84-multilingual-smart-search-debug-apk`

Release artifact:

`be-lahza-v84-release-aab-apk`


## V8.5 Android Build Fix

Fixed FirebasePhoneLoginActivity Java compile error from the V8.4 GitHub Actions build.

Debug artifact:

`be-lahza-v85-buildfix-multilingual-search-debug-apk`
