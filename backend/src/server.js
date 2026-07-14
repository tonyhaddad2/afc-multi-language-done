import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import pino from 'pino-http';
import rateLimit from 'express-rate-limit';

import { config, requireConfig } from './config.js';
import { requireAuth } from './auth.js';
import { authRouter } from './routes/auth.js';
import { usersRouter } from './routes/users.js';
import { medicalRouter } from './routes/medical.js';
import { trustedContactsRouter } from './routes/trustedContacts.js';
import { emergenciesRouter } from './routes/emergencies.js';
import { configRouter } from './routes/config.js';
import { adminRouter } from './routes/admin.js';

requireConfig();

const app = express();
app.disable('x-powered-by');
app.set('trust proxy',1);
app.use(helmet({ crossOriginResourcePolicy:{ policy:'same-site' } }));
app.use(cors({ origin: config.allowedOrigins.length ? config.allowedOrigins : false }));
app.use(express.json({ limit:'128kb' }));
app.use(pino({
  redact:[
    'req.headers.authorization',
    'req.body.idToken',
    'req.body.phone',
    'req.body.medicalIdText',
    'req.body.note',
    'req.body.location',
    'res.headers["set-cookie"]'
  ]
}));
app.use(rateLimit({
  windowMs:config.rateLimitWindowMs,
  max:config.rateLimitMax,
  standardHeaders:true,
  legacyHeaders:false
}));

app.get('/health',(_,res) => res.json({
  ok:true,
  service:'be-lahza-backend',
  version:'0.9.0',
  environment:config.env
}));

app.use('/v1/auth',authRouter);
app.use('/v1/config',configRouter);
app.use('/v1/users',requireAuth,usersRouter);
app.use('/v1/medical-id',requireAuth,medicalRouter);
app.use('/v1/trusted-contacts',requireAuth,trustedContactsRouter);
app.use('/v1/emergencies',requireAuth,emergenciesRouter);
app.use('/v1/admin',requireAuth,adminRouter);

app.use((err,req,res,next) => {
  req.log?.error(err);
  if (err?.name === 'ZodError') return res.status(400).json({ error:'invalid_request',details:err.errors });
  res.status(500).json({ error:'internal_error' });
});

app.listen(config.port,() => console.log(`be lahza backend listening on ${config.port}`));
