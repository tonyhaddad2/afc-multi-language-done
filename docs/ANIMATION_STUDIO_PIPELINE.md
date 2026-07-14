# Production Animation Studio Pipeline

The current MP4s are app-ready mannequin-style assets. For a final world-class animation pass, use this pipeline:

## Visual direction

- clean clay/mannequin humans
- soft medical studio lighting
- cyan/white clean floor
- no gore
- clear hand positions
- clear body mechanics
- consistent camera height and focal length
- short loop and full lesson export for every maneuver

## Required source files

For each maneuver:

- Blender/Maya/Cinema4D scene file
- rigged rescuer mannequin
- rigged patient mannequin
- camera
- lights
- timeline markers for each first-aid step
- rendered MP4
- compressed Android MP4
- preview thumbnail

## Export targets

- `app/src/main/res/raw/<maneuver>_guide.mp4`
- H.264 MP4
- 720p or 1080p
- mobile compressed
- no audio unless app supports language audio tracks
- 5–15s loop for guide
- optional 30–60s full lesson later

## File list

- allergic_guide.mp4
- bleeding_guide.mp4
- breathing_guide.mp4
- burns_guide.mp4
- choking_guide.mp4
- cpr_guide.mp4
- crash_guide.mp4
- drowning_guide.mp4
- earthquake_guide.mp4
- electric_guide.mp4
- fire_guide.mp4
- panic_guide.mp4
- poisoning_guide.mp4
- seizure_guide.mp4
- stroke_guide.mp4
- unconscious_guide.mp4
