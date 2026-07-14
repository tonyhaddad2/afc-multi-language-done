# Firebase Setup

## 1. Create Firebase project

Create a Firebase project for be lahza and add an Android app with package:

```text
com.belahza.app
```

Download:

```text
google-services.json
```

Place it here:

```text
app/google-services.json
```

The repository includes:

```text
app/google-services.json.example
```

Do not commit real production secrets into a public repository.

## 2. Android build behavior

`app/build.gradle` applies the Google Services plugin only if `app/google-services.json` exists:

```gradle
if (file('google-services.json').exists()) {
    apply plugin: 'com.google.gms.google-services'
}
```

So debug builds still work before Firebase is configured.

## 3. Firebase phone login

Open the app screen:

```text
Firebase Login
```

Flow:

1. Enter API base URL.
2. Enter phone number.
3. Send OTP.
4. Verify code.
5. App exchanges Firebase ID token with `/v1/auth/firebase`.
6. Backend returns be lahza bearer token.
7. App registers FCM token with backend.

## 4. Firebase Cloud Messaging backend

Backend env var:

```bash
FCM_SERVICE_ACCOUNT_JSON='{"type":"service_account",...}'
```

The backend sends privacy-safe notifications only:

```text
Emergency alert. Tap to view authorized emergency information.
```

Raw medical and GPS data are not placed inside notification body.
