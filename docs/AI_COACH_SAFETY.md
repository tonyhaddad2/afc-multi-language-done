# AI Coach safety and calibration notes

V5 adds rescuer lock calibration and patient/lying-body rejection logic.

The coach now asks the user to frame the rescuer, tap **Lock rescuer**, and then practice. This helps reduce the previous issue where pose detection may lock onto the patient or mannequin. The scoring checks:

- rescuer landmark visibility
- horizontal/lying-body rejection
- locked rescuer size and position drift
- mode-specific posture and hand-position feedback
- CPR rhythm estimate for CPR/drowning CPR modes

This is still a training/prototype system. A reliable life-safety AI coach requires Red Cross instructor review, controlled camera setup guidance, labeled video data, testing across body types, clothing, lighting, phone models, languages, and emergency scenarios.
