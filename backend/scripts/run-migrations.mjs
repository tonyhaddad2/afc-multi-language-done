import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import pg from 'pg';
import dotenv from 'dotenv';

dotenv.config();

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');
const migrationsDir = path.join(root, 'migrations');
const sqlDir = path.join(root, 'sql');
const databaseUrl = process.env.DATABASE_URL;

if (!databaseUrl) {
  console.error('Missing DATABASE_URL');
  process.exit(1);
}

const client = new pg.Client({ connectionString: databaseUrl, ssl: process.env.PGSSL === 'true' ? { rejectUnauthorized: false } : undefined });
await client.connect();

await client.query(`
  create table if not exists schema_migrations (
    filename text primary key,
    applied_at timestamptz not null default now()
  )
`);

let files = [];
if (fs.existsSync(migrationsDir)) {
  files = fs.readdirSync(migrationsDir).filter(f => f.endsWith('.sql')).sort().map(f => path.join(migrationsDir, f));
}
if (!files.length && fs.existsSync(path.join(sqlDir, 'schema.sql'))) {
  files = [path.join(sqlDir, 'schema.sql')];
}

for (const file of files) {
  const filename = path.basename(file);
  const exists = await client.query('select filename from schema_migrations where filename=$1', [filename]);
  if (exists.rows.length) {
    console.log(`skip ${filename}`);
    continue;
  }
  console.log(`apply ${filename}`);
  const sql = fs.readFileSync(file, 'utf8');
  await client.query('begin');
  try {
    await client.query(sql);
    await client.query('insert into schema_migrations(filename) values($1)', [filename]);
    await client.query('commit');
  } catch (err) {
    await client.query('rollback');
    throw err;
  }
}

await client.end();
console.log('migrations complete');
