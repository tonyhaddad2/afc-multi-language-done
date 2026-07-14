import express from 'express';
import { z } from 'zod';
import { query } from '../db.js';
import { requireAdmin } from '../adminAuth.js';
import { config } from '../config.js';

export const adminRouter = express.Router();
adminRouter.use(requireAdmin);

adminRouter.get('/metrics', async (req, res) => {
  const users = await query('select count(*)::int as count from users where disabled_at is null');
  const active = await query(`select count(*)::int as count from emergency_sessions where status='active'`);
  const sessions = await query(`select status, count(*)::int as count from emergency_sessions group by status`);
  const notifications = await query(`select delivery_status, count(*)::int as count from emergency_notifications group by delivery_status`);
  const audit = await query(`select count(*)::int as count from audit_logs where created_at > now() - interval '24 hours'`);
  res.json({
    ok: true,
    env: config.env,
    users: users.rows[0]?.count || 0,
    activeEmergencies: active.rows[0]?.count || 0,
    sessionsByStatus: sessions.rows,
    notificationsByStatus: notifications.rows,
    auditEvents24h: audit.rows[0]?.count || 0
  });
});

adminRouter.put('/config/:key', async (req, res) => {
  const body = z.object({ value: z.any() }).parse(req.body);
  await query(
    `insert into app_config(key, value, updated_at)
     values($1,$2,now())
     on conflict(key) do update set value=excluded.value, updated_at=now()`,
    [req.params.key, body.value]
  );
  res.json({ ok: true });
});

adminRouter.post('/notifications/:id/mark-sent', async (req, res) => {
  await query('update emergency_notifications set delivery_status=$1, sent_at=now() where notification_id=$2', ['sent', req.params.id]);
  res.json({ ok: true });
});
