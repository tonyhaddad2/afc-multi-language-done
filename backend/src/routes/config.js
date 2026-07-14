import express from 'express';
import { query } from '../db.js';

export const configRouter = express.Router();

configRouter.get('/public', async (req, res) => {
  const r = await query('select key, value from app_config');
  const out = {};
  for (const row of r.rows) out[row.key] = row.value;
  res.json({ config: out });
});
