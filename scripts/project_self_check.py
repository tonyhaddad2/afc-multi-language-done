from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
required = [
    ".github/workflows/android-build.yml",
    "app/build.gradle",
    "app/src/main/AndroidManifest.xml",
    "app/src/main/java/com/belahza/app/MainActivity.java",
    "app/src/main/java/com/belahza/app/EmergencyMode.java",
    "app/src/main/java/com/belahza/app/EmergencyCountdownActivity.java",
    "app/src/main/java/com/belahza/app/LiveLocationService.java",
    "app/src/main/java/com/belahza/app/TrustedContactsActivity.java",
    "app/src/main/java/com/belahza/app/TrustedEmergencyActivity.java",
    "backend/src/server.js",
    "backend/sql/schema.sql",
]
missing = [item for item in required if not (root/item).exists()]
if missing:
    raise SystemExit("Missing required files:\n" + "\n".join(missing))

ET.parse(root/"app/src/main/AndroidManifest.xml")

bad = []
for java in (root/"app/src/main/java").rglob("*.java"):
    text = java.read_text(encoding="utf-8")
    if "FirebaseAuthInvalidCredentialsException" in text:
        bad.append(f"{java}: obsolete checked exception")
    if text.count("{") != text.count("}"):
        bad.append(f"{java}: unbalanced braces")
if bad:
    raise SystemExit("\n".join(bad))

videos = list((root/"app/src/main/res/raw").glob("*_guide.mp4"))
if len(videos) < 16:
    raise SystemExit(f"Expected at least 16 guide videos, found {len(videos)}")

main = (root/"app/src/main/java/com/belahza/app/MainActivity.java").read_text(encoding="utf-8")
for forbidden in ["AdminDashboardActivity.class", "ProductionTestActivity.class", "AuthActivity.class"]:
    if forbidden in main:
        raise SystemExit(f"Public MainActivity exposes developer feature: {forbidden}")

print("be lahza project self-check passed")
