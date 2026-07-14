#!/usr/bin/env sh
set -eu

IN="${1:?input mp4 required}"
OUT="${2:?output mp4 required}"

ffmpeg -y -i "$IN" \
  -vf "scale=1280:-2,fps=24" \
  -c:v libx264 \
  -profile:v high \
  -pix_fmt yuv420p \
  -crf 23 \
  -preset slow \
  -movflags +faststart \
  -an \
  "$OUT"

echo "Android-ready MP4 written to $OUT"
