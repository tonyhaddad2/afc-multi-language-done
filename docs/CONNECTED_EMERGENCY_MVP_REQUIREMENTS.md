# be lahza V7 Connected Emergency MVP — Development and Security Requirements

## 1. Core security requirement

All sensitive user data must be encrypted in transit and at rest. The backend/server must handle controlled decryption only when needed to process emergency alerts, trusted-contact access, Medical ID snapshots, and user-authorized data sharing.

The app must not store plain medical or location data openly on the device or in the database. Sensitive fields must be encrypted before persistence and decrypted only by authorized server-side flows.

## 2. Functional scope for the connected MVP

The connected version of be lahza must include the following core modules:

- Authentication
- User profile
- Medical ID
- Trusted contacts
- Emergency trigger
- Emergency active session
- Live location sharing
- Lock-screen Medical ID screen
- Emergency notifications
- First-aid guides
- 3D animation lessons
- AI Coach
- Settings
- Offline mode

The current app already includes many training and emergency-support features locally, but the connected MVP requires backend integration for identity, sharing, alerts, and secure data handling.

## 3. Recommended backend architecture

### Simplest MVP

- Firebase Authentication
- Firestore or Realtime Database
- Firebase Cloud Functions
- Firebase Cloud Messaging
- Firebase Storage if needed

### More professional production version

- Node.js or NestJS backend
- PostgreSQL
- Redis for emergency sessions
- Firebase Cloud Messaging for push notifications
- Cloud storage for assets
- KMS for encryption keys

## 4. Authentication and account requirements

The backend must support:

- Phone number login
- Optional email login
- User ID creation
- Device registration
- Push notification token registration
- Session refresh
- Logout
- Local session deletion

Minimum user data model:

- user_id
- phone_number
- email
- name
- profile_photo_url
- preferred_language
- created_at
- last_seen_at
- account_status

## 5. Encryption requirements

Sensitive data must be handled according to the following rules:

- All sensitive data must be encrypted before it is stored in the database.
- Data must be transmitted only over HTTPS/TLS.
- The backend must decrypt sensitive data only inside trusted server functions when required.
- Encryption keys must never be stored directly inside the app or database.

Sensitive fields include:

- Medical ID
- Allergies
- Medications
- Conditions
- Emergency contacts
- GPS locations
- Emergency sessions
- Trusted contact relations
- Phone numbers
- Emergency notes

### Approved encryption model

- AES-256-GCM for field encryption
- TLS for network transfer
- KMS for key management
- Rotating encryption keys
- Separate keys for sensitive categories

Example field storage model:

- medical_id.blood_type = encrypted
- medical_id.allergies = encrypted
- medical_id.medications = encrypted
- emergency_location.lat = encrypted
- emergency_location.lng = encrypted

## 6. Server-side decryption rules

The backend must decrypt data only for approved actions:

- User opens their own Medical ID
- User edits their Medical ID
- Emergency is triggered
- Trusted contact opens an active emergency
- Emergency Medical ID screen is opened
- Emergency alert snapshot is created

The server must not decrypt everything broadly or continuously. The correct model is:

- Decrypt only what is needed
- Decrypt only for authorized users
- Decrypt only during active emergency flows
- Log every sensitive access

## 7. Medical ID backend requirements

Medical ID must be stored encrypted. The backend must support:

- createMedicalID()
- updateMedicalID()
- getOwnMedicalID()
- createEmergencyMedicalIDSnapshot()
- getEmergencyMedicalIDForTrustedContact()

The Medical ID data model should include:

- full_name
- date_of_birth
- age
- blood_type
- allergies
- conditions
- medications
- implants
- pregnancy_status
- disabilities
- doctor_name
- doctor_phone
- emergency_notes
- preferred_hospital
- emergency_contacts
- last_updated_at

## 8. Emergency snapshot requirement

When an emergency is triggered, the backend must create a snapshot that remains stable for the duration of the emergency. This prevents changing or partially missing data during a live incident.

Snapshot fields:

- emergency_id
- user_id
- encrypted_medical_id_snapshot
- created_at
- visible_fields

Visible fields should be limited to the minimum necessary, for example:

- name
- blood type
- allergies
- conditions
- medications
- emergency contacts

## 9. Lock-screen Medical ID requirement

When an emergency is triggered, the app must show a high-priority lock-screen notification stating that emergency Medical ID information is available.

The notification should open an emergency screen, such as EmergencyMedicalIdActivity, and show only authorized emergency information. The notification text must not expose full medical details.

Required Android implementation points:

- POST_NOTIFICATIONS permission
- High-priority notification channel
- Public lock-screen notification
- EmergencyMedicalIdActivity
- showWhenLocked support
- turnScreenOn support if allowed
- Ongoing emergency notification

The notification text should be limited to something like:

- Emergency Medical ID available
- Tap to view emergency information

## 10. Trusted contacts system

The app must support:

- Trusted contacts list
- Add trusted contact
- Invite pending
- Invite accepted
- Remove trusted contact
- Blocked contacts
- Emergency priority order

Backend tables:

- trusted_contacts
- trusted_contact_invites
- blocked_users

Required functions:

- sendTrustedContactInvite()
- acceptInvite()
- rejectInvite()
- removeTrustedContact()
- blockUser()
- getTrustedContacts()

## 11. Emergency trigger and session requirements

The app must support:

- SOS trigger screen
- Emergency type selector
- Countdown screen
- Active emergency screen
- Cancel emergency screen
- I am safe button
- Resolved button

Emergency types include:

- Medical
- Cardiac arrest
- Choking
- Bleeding
- Fire
- Car accident
- Electric shock
- Drowning
- Panic
- Other

Backend object:

- emergency_sessions

Required fields:

- emergency_id
- user_id
- emergency_type
- status: active / cancelled / resolved
- started_at
- ended_at
- last_location_at
- medical_id_snapshot_id
- trusted_contacts_notified
- battery_level
- network_status
- manual_note

Required functions:

- createEmergencySession()
- cancelEmergencySession()
- resolveEmergencySession()
- getActiveEmergency()
- notifyTrustedContacts()

## 12. Push notifications and payload rules

Push notifications must be sent for:

- Emergency triggered
- Emergency cancelled
- Emergency resolved
- Location updated
- Trusted contact invite
- Trusted contact accepted
- Medical ID available
- Low battery during emergency

Push payloads must not contain raw medical data. They should only trigger an app action or provide a short alert such as:

- Emergency alert from Rambo. Tap to view authorized emergency info.

## 13. Live location sharing requirements

The app must support:

- GPS location capture at emergency start
- Updates every 15–30 seconds
- Secure transmission to the backend
- Trusted contacts viewing live map access
- Stopping location sharing when the emergency ends

Android requirements:

- Fine location permission
- Foreground service
- Ongoing notification
- Battery optimization handling
- Last known location fallback
- Manual location note

## 14. Offline mode requirements

The app must remain useful without internet access. Offline capabilities include:

- Emergency numbers
- Medical ID local copy
- SOS share message
- First-aid guides
- 3D animations
- AI Coach
- CPR timer
- Manual location note

If no internet is available, the app should:

- Prepare WhatsApp/SMS/share message
- Show emergency numbers
- Store emergency session locally
- Sync later when internet returns

## 15. Local secure storage requirements

On-device sensitive cache data must be stored securely using:

- EncryptedSharedPreferences
- Encrypted local database if needed
- Android Keystore

Local sensitive data includes:

- Auth token
- User settings
- Medical ID cached copy
- Trusted contacts cached copy
- Last emergency draft

Sensitive data must never be stored in plain SharedPreferences.

## 16. Backend database minimum schema

Minimum collections/tables:

- users
- user_profiles
- medical_ids
- medical_id_snapshots
- trusted_contacts
- trusted_contact_invites
- device_tokens
- emergency_sessions
- emergency_locations
- emergency_status_updates
- emergency_notifications
- user_settings
- app_config
- audit_logs

## 17. Backend functions minimum list

- createUserProfile
- updateUserProfile
- registerDeviceToken
- updateMedicalID
- getOwnMedicalID
- sendTrustedContactInvite
- acceptTrustedContactInvite
- removeTrustedContact
- createEmergencySession
- createMedicalIDSnapshot
- notifyTrustedContacts
- updateEmergencyLocation
- getEmergencyForTrustedContact
- markAlertSeen
- markHelping
- cancelEmergency
- resolveEmergency
- expireOldSessions
- deleteUserData

## 18. Android permissions and notification channels

Required Android permissions:

- INTERNET
- CAMERA
- ACCESS_FINE_LOCATION
- ACCESS_COARSE_LOCATION
- POST_NOTIFICATIONS
- VIBRATE
- FOREGROUND_SERVICE
- FOREGROUND_SERVICE_LOCATION
- WAKE_LOCK

Required notification channels:

- Emergency Alerts
- Medical ID Lock Screen
- Location Sharing Active
- Trusted Contact Invites
- General Updates

## 19. Testing and acceptance criteria

Functional testing should confirm that:

- Login works
- Trusted contact invite works
- Emergency alert reaches contact
- Push notification opens the correct screen
- Live location updates
- Medical ID decrypts correctly for allowed users
- Medical ID does not show for unauthorized users
- Lock-screen notification appears
- Emergency cancellation works
- Offline fallback works
- AI Coach starts camera
- Guide animations play

Security testing should confirm that:

- Unauthorized users cannot read Medical ID
- Unauthorized users cannot read location
- Database stores encrypted fields
- Server decrypts only in approved functions
- Push notifications contain no raw medical data
- Local cache is encrypted

## 20. Development roadmap

### V7 — Connected MVP

Build:

- Firebase login
- User profile
- Encrypted Medical ID
- Trusted contacts
- Push notifications
- Emergency sessions
- Live location
- Lock-screen Medical ID notification
- Emergency Medical ID screen
- Cancel/resolve emergency

### V8 — Better emergency system

- Trusted contact live emergency dashboard
- I saw alert / I am helping
- Emergency history
- Offline sync
- Notification reliability

### V9 — Content quality

- Real 3D animations
- Better lessons
- Voice and subtitles
- Animation timestamp matching
- AI Coach improvements

### V10 — Production hardening

- Encryption key rotation
- Audit logs
- Monitoring dashboard
- Crash reporting
- Rate limits
- Backup and recovery
- Multi-language polish
- Release signing

## 21. Final requirement statement

be lahza must use a secure backend where all sensitive medical, identity, contact, and location data is encrypted at rest and transmitted over HTTPS/TLS. The backend server is responsible for controlled decryption only when an authorized action requires it, such as showing a user their own Medical ID, creating an emergency Medical ID snapshot, sending trusted-contact emergency access, or displaying live emergency location to accepted trusted contacts.
