from pathlib import Path

manifest = Path("android/app/src/main/AndroidManifest.xml")
xml = manifest.read_text(encoding="utf-8")

provider_class = "androidx.core.content.FileProvider"
if provider_class not in xml:
    provider = (
        '<provider android:name="androidx.core.content.FileProvider" '
        'android:authorities="${applicationId}.fileprovider" '
        'android:exported="false" android:grantUriPermissions="true">'
        '<meta-data android:name="android.support.FILE_PROVIDER_PATHS" '
        'android:resource="@xml/file_paths" />'
        '</provider>'
    )
    if "</application>" not in xml:
        raise SystemExit("Could not find </application> in AndroidManifest.xml")
    xml = xml.replace("</application>", provider + "</application>", 1)
    manifest.write_text(xml, encoding="utf-8")
