import { query } from './db.js';

export async function audit(actorUserId, action, targetType = null, targetId = null, reason = null) {
  try {
    await query('insert into audit_logs(actor_user_id, action, target_type, target_id, reason) values($1,$2,$3,$4,$5)', [actorUserId || null, action, targetType, targetId, reason]);
  } catch (err) {
    // audit failure must not break emergency flow; logs still capture the error.
  }
}
