# Release Signing and Play Store AAB

## 1. Create your private upload keystore

Run locally:

```bash
scripts/create-release-keystore.sh be-lahza-release.jks be_lahza_release
```

Keep the keystore private. Do not upload it to chat or commit it to GitHub.

## 2. Add GitHub Secrets

Add these repository secrets:

```text
BE_LAHZA_KEYSTORE_BASE64
BE_LAHZA_KEYSTORE_PASSWORD
BE_LAHZA_KEY_ALIAS
BE_LAHZA_KEY_PASSWORD
GOOGLE_SERVICES_JSON_BASE64
```

Create base64 values:

```bash
base64 -w 0 be-lahza-release.jks
base64 -w 0 app/google-services.json
```

## 3. Build release artifacts

Run:

```text
Actions -> Build be lahza Release AAB -> Run workflow
```

Artifacts:

```text
app-release.aab
app-release.apk
```

Debug APK workflow remains available separately.
