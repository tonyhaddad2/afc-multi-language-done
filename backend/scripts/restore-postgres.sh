#!/usr/bin/env sh
set -eu

: "${DATABASE_URL:?DATABASE_URL is required}"
DUMP="${1:?Usage: restore-postgres.sh backup.dump}"

pg_restore "$DUMP" --clean --if-exists --no-owner --no-acl --dbname "$DATABASE_URL"

echo "Restore completed from $DUMP"
