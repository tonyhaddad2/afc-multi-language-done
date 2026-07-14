# Remaining external configuration

The repository is deployment-ready, but these values cannot be manufactured safely inside a public ZIP:

1. A real Firebase project and `app/google-services.json`.
2. A real Firebase service-account JSON stored as a backend secret.
3. A real HTTPS backend domain.
4. A production PostgreSQL database and `DATABASE_URL`.
5. Production JWT, field-encryption, phone-lookup, and old-key rotation secrets.
6. A private Android release keystore owned by the publisher.
7. Real two-phone notification, lock-screen, background-location, and battery-saver testing.
8. Final manual review/refinement of 3D source animation by a professional animator and first-aid instructor.

Without those external values the APK remains fully usable for local guides, numbers, Medical ID, location acquisition, calls, and share fallback, while connected features show a safe unavailable/offline state instead of pretending delivery succeeded.
