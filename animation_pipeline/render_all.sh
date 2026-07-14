#!/usr/bin/env sh
set -eu

MANEUVERS="cpr choking bleeding burns seizure unconscious allergic fire"

for m in $MANEUVERS; do
  blender --background --python animation_pipeline/blender_scene_generator.py -- "$m"
done

echo "Generated Blender template scenes. Open each .blend for manual rig/keyframe refinement."
