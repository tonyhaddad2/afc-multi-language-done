#!/usr/bin/env sh
set -eu

OUT="${1:-be-lahza-release.jks}"
ALIAS="${2:-be_lahza_release}"

echo "This creates YOUR private upload key. Keep it safe and never send it in chat."
keytool -genkeypair \
  -v \
  -keystore "$OUT" \
  -alias "$ALIAS" \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000

echo ""
echo "Created: $OUT"
echo "Alias: $ALIAS"
echo ""
echo "Convert keystore to GitHub secret:"
echo "base64 -w 0 $OUT"
