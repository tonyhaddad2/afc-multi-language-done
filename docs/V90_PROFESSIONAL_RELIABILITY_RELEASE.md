# be lahza V9.0 Professional Reliability Release

This release is a cleanup and reliability rebuild of the earlier prototype.

## Public product changes

- Replaced the crowded developer dashboard with a focused public home screen.
- Kept only SOS, emergency search, numbers, trusted contacts, Medical ID, settings, and language in the public UI.
- Moved backend/admin/test tools to a debug-only Developer Hub.
- Replaced fixed two-column emergency cards with responsive single-column cards.
- Removed forced portrait orientation from the manifest.
- Added content descriptions and minimum touch heights to important controls.

## Emergency flow

- Added a three-second cancelable countdown.
- Added an active-emergency guard to prevent repeated triggers.
- Added a client request ID for backend idempotency.
- Added backend unique constraint on `(user_id, client_request_id)`.
- Removed the double-trigger path between SOS and Alert screens.
- Separated connected alert activation from manual SMS/share fallback.
- Added clear creating, active, offline-queued, notified, resolving, and cancelled states.

## Location

- Replaced repeated `getLastKnownLocation()` polling with Fused Location Provider updates.
- Added accuracy and age checks.
- Added fresh/stale location status.
- Location service starts immediately when emergency mode begins.
- Failed location updates are queued securely for retry.

## Offline queue

- Queue items now have IDs and client request IDs.
- Successful items are removed individually.
- Failed items stay queued without resubmitting successful items.
- Offline-created emergency sessions attach their returned server ID to the active local session.
- Idempotency prevents duplicate emergency sessions.

## Trusted contacts and notifications

- Push notifications now route directly to the emergency receiver.
- Receiver reads the emergency ID from the notification intent.
- Replaced raw JSON with a structured emergency viewer.
- Added location, allowed Medical ID fields, status, directions, and response actions.
- Added connected invitation list, accept/reject, accepted contact list, and remove action.
- Removed manual invitation UUID entry from the public flow.
- Firebase messages are data-only so the app controls the correct destination.

## Medical ID

- Added canonical Medical ID field names.
- Added local visible-field filtering.
- Added backend visible-field filtering before trusted-contact response.
- Lock-screen Medical ID now exits unless a real emergency is active.
- Removed lock-screen Medical ID from the public home screen.
- Added encrypted phone fields plus phone hash lookup in the backend.

## AI Coach

- Kept the coach camera-only.
- Removed video overlay from the camera.
- Restricted scored feedback to visible CPR posture and approximate rhythm.
- Removed misleading generic numeric scores for unsupported maneuvers.
- Added explicit limits: no compression depth, force, recoil, or patient-response measurement.
- Added lighting, framing, too-close, too-far, arm, alignment, rhythm, and rescuer-tracking feedback.
- Unsupported maneuvers show visual-guided practice only.

## Lessons and animations

- Removed duplicate Canvas animation from the guide screen.
- The guide now uses one consistent visual system.
- Step changes are user-controlled instead of automatically drifting out of sync with the video.
- Bundled clips were converted to language-neutral visual footage by removing baked-in English title/footer areas.
- Written steps are localized and displayed separately.

## Localization and search

- Public emergency screens use English, French, or Arabic.
- Added complete translated emergency steps and warnings.
- Arabic layout direction is applied to public screens.
- Smart search retains fuzzy typo matching and multilingual synonyms.

## Backend

- Added idempotent emergency creation.
- Added encrypted canonical Medical ID snapshots.
- Added encrypted fresh location records.
- Added trusted-contact authorization and filtered Medical ID responses.
- Added encrypted user phone storage and HMAC phone lookup.
- Added privacy-safe data-only FCM payloads.
