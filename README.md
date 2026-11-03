# Anime File Manager

Android app for organizing a personal anime collection and locally stored episode files.

## Build APK
Every push to `main` or `master` starts the GitHub Actions workflow **Anime File Manager - Build APK**. You can also run it manually from Actions.

When the workflow succeeds, open its run and download the artifact named **anime-file-manager-apk**. The APK is a debug build for testing and sideload installation.

## Project
- Capacitor 6 / Android
- Local episode storage using IndexedDB
- External player hand-off through the Android share/open intent
- Watch status and anime collection tracking

Note: no app can guarantee playback of every codec/container in its built-in browser player. Use an installed compatible player for unsupported formats.
