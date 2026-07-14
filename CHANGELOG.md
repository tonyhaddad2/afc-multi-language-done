# Changelog

## 0.9.0 — Professional Reliability Release

- Public UI reduced to emergency essentials.
- Developer/admin features moved behind debug-only Developer Hub.
- Added cancelable SOS countdown and active-session guard.
- Added backend idempotency key and database uniqueness.
- Replaced stale last-known-location loop with Fused Location Provider updates.
- Rebuilt offline queue to remove successful items individually.
- Fixed FCM routing to trusted emergency receiver.
- Rebuilt trusted-contact invitation and receiver UI.
- Enforced Medical ID visible fields locally and on backend.
- Guarded lock-screen Medical ID behind active emergency mode.
- Restricted AI scoring to visible CPR posture/cadence checks.
- Removed unsupported maneuver scores and camera video overlay.
- Rebuilt guide/lesson flow around one consistent visual system.
- Added full public English/French/Arabic emergency steps and warnings.
- Removed fixed portrait requirement and public developer clutter.
- Added encrypted phone storage plus HMAC lookup on backend.
- Removed the previous Firebase checked-exception compilation failure.
