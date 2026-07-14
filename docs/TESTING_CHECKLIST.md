# Testing Checklist

## Android

- Build APK
- Open app
- Permission setup screen
- Backend health check
- Dev token request in non-production
- Medical ID entry
- Medical ID sync
- Trusted contact invite
- Accept trusted contact
- Trigger emergency
- Lock-screen Medical ID notification
- Active emergency screen
- Live location service starts after backend emergency ID
- Offline queue when backend is unavailable
- Retry offline queue
- Trusted receiver loads emergency
- Trusted receiver marks seen/helping/cannot help
- AI Coach camera opens and auto-locks rescuer
- No animation overlays the AI camera
- Guide/lesson animations play

## Backend

- `npm run check`
- `npm test`
- `/health`
- `/v1/auth/dev-token`
- `/v1/users/profile`
- `/v1/medical-id`
- `/v1/trusted-contacts/invite`
- `/v1/trusted-contacts/accept`
- `/v1/emergencies`
- `/v1/emergencies/:id/location`
- `/v1/emergencies/:id/status`
- `/v1/config/public`
- `/v1/admin/metrics`

## Security

- Database does not store raw Medical ID text
- Database does not store raw location coordinates
- Push payload does not contain raw medical/location data
- Unauthorized users cannot open emergency data
- Only accepted trusted contacts can view active emergency data
- Audit logs are written for sensitive decrypt actions
