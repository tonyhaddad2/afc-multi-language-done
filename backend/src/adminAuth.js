import { config } from './config.js';

export function requireAdmin(req, res, next) {
  if (!config.adminUserIds.length) return res.status(403).json({ error: 'admin_not_configured' });
  if (!req.user?.id || !config.adminUserIds.includes(req.user.id)) {
    return res.status(403).json({ error: 'admin_only' });
  }
  next();
}
