alter table users add column if not exists phone_hash text;
alter table users add column if not exists encrypted_phone text not null default '';
create unique index if not exists idx_users_phone_hash on users(phone_hash) where phone_hash is not null;

alter table trusted_contact_invites add column if not exists invited_phone_hash text;
alter table trusted_contact_invites add column if not exists encrypted_invited_phone text not null default '';
create index if not exists idx_invites_phone_hash_status on trusted_contact_invites(invited_phone_hash,status);

alter table emergency_sessions add column if not exists client_request_id text;
alter table emergency_sessions add column if not exists encrypted_initial_location text not null default '';
alter table emergency_sessions add column if not exists visible_fields text[] not null default array['fullName','bloodType','allergies','conditions','medications','emergencyContacts'];
create unique index if not exists idx_emergency_idempotency on emergency_sessions(user_id,client_request_id) where client_request_id is not null;
