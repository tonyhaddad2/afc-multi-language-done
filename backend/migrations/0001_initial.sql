create extension if not exists pgcrypto;

create table if not exists users (
  user_id text primary key,
  phone_hash text unique,
  encrypted_phone text not null default '',
  encrypted_profile text not null default '',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  disabled_at timestamptz
);

create table if not exists device_tokens (
  token text primary key,
  user_id text not null references users(user_id) on delete cascade,
  platform text not null default 'android',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  revoked_at timestamptz
);

create table if not exists medical_ids (
  user_id text primary key references users(user_id) on delete cascade,
  encrypted_medical_id text not null,
  visible_fields text[] not null default array['fullName','bloodType','allergies','conditions','medications','emergencyContacts'],
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists trusted_contact_invites (
  invite_id uuid primary key default gen_random_uuid(),
  owner_user_id text not null references users(user_id) on delete cascade,
  invited_phone_hash text not null,
  encrypted_invited_phone text not null,
  accepted_user_id text references users(user_id) on delete set null,
  status text not null check(status in ('pending','accepted','rejected','expired')),
  created_at timestamptz not null default now(),
  accepted_at timestamptz,
  rejected_at timestamptz
);

create table if not exists trusted_contacts (
  owner_user_id text not null references users(user_id) on delete cascade,
  contact_user_id text not null references users(user_id) on delete cascade,
  status text not null check(status in ('accepted','removed','blocked')),
  priority integer not null default 100,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  primary key(owner_user_id, contact_user_id)
);

create table if not exists emergency_sessions (
  emergency_id uuid primary key default gen_random_uuid(),
  user_id text not null references users(user_id) on delete cascade,
  client_request_id text not null,
  emergency_type text not null,
  encrypted_note text not null default '',
  encrypted_initial_location text not null default '',
  encrypted_medical_snapshot text not null,
  visible_fields text[] not null,
  status text not null check(status in ('active','cancelled','resolved')),
  started_at timestamptz not null default now(),
  ended_at timestamptz,
  last_location_at timestamptz,
  trusted_contacts_notified integer not null default 0,
  unique(user_id, client_request_id)
);

create table if not exists emergency_locations (
  location_id uuid primary key default gen_random_uuid(),
  emergency_id uuid not null references emergency_sessions(emergency_id) on delete cascade,
  user_id text not null references users(user_id) on delete cascade,
  encrypted_location text not null,
  accuracy numeric,
  battery_level numeric,
  source text not null default 'gps',
  created_at timestamptz not null default now()
);

create table if not exists emergency_status_updates (
  update_id uuid primary key default gen_random_uuid(),
  emergency_id uuid not null references emergency_sessions(emergency_id) on delete cascade,
  actor_user_id text not null references users(user_id) on delete cascade,
  status text not null check(status in ('seen','helping','cannot_help','comment','cancelled','resolved')),
  encrypted_note text not null default '',
  created_at timestamptz not null default now()
);

create table if not exists emergency_notifications (
  notification_id uuid primary key default gen_random_uuid(),
  emergency_id uuid references emergency_sessions(emergency_id) on delete cascade,
  recipient_user_id text not null references users(user_id) on delete cascade,
  type text not null,
  delivery_status text not null default 'queued',
  created_at timestamptz not null default now(),
  sent_at timestamptz,
  error text
);

create table if not exists app_config (
  key text primary key,
  value jsonb not null,
  updated_at timestamptz not null default now()
);

create table if not exists audit_logs (
  audit_id uuid primary key default gen_random_uuid(),
  actor_user_id text,
  action text not null,
  target_type text,
  target_id text,
  reason text,
  created_at timestamptz not null default now()
);

create index if not exists idx_device_tokens_user on device_tokens(user_id);
create index if not exists idx_invites_phone_status on trusted_contact_invites(invited_phone_hash,status);
create index if not exists idx_trusted_contacts_contact on trusted_contacts(contact_user_id,status);
create index if not exists idx_emergency_user_status on emergency_sessions(user_id,status);
create index if not exists idx_emergency_locations_time on emergency_locations(emergency_id,created_at desc);
create index if not exists idx_emergency_updates_time on emergency_status_updates(emergency_id,created_at desc);
create index if not exists idx_notifications_recipient on emergency_notifications(recipient_user_id,delivery_status);

insert into app_config(key,value) values
  ('emergency_numbers','{"red_cross":"140","police":"112","civil_defense":"125","fire":"175"}'::jsonb),
  ('feature_flags','{"connected_alerts":true,"live_location":true,"lock_screen_medical_id":true,"ai_cpr_coach":true}'::jsonb),
  ('minimum_app_version','{"android_version_code":90}'::jsonb),
  ('maintenance','{"enabled":false,"message":""}'::jsonb)
on conflict(key) do nothing;
