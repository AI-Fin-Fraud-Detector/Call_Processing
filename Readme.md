# MyCall — A Sip / Calling Application

A modern open-source Android calling application built with Jetpack Compose. Forked from [amadz](https://github.com/amadz) and extended with authentication, fraud reporting, and remote management capabilities.

## Installation

[<img src="https://raw.githubusercontent.com/rubenpgrady/get-it-on-github/refs/heads/main/get-it-on-github.png"
    alt="Get it on GitHub"
    height="80">](https://github.com/AI-Fin-Fraud-Detector/Call_Processing/releases/latest)
[<img src="https://github.com/user-attachments/assets/713d71c5-3dec-4ec4-a3f2-8d28d025a9c6"
    alt="Get it on Obtainium"
    height="80">](https://apps.obtainium.imranr.dev/redirect?r=obtainium://add/https://github.com/AI-Fin-Fraud-Detector/Call_Processing)
[<img src="https://raw.githubusercontent.com/mueller-ma/android-common/main/assets/direct-apk-download.png"
    alt="Direct APK Download"
    height="80">](https://github.com/AI-Fin-Fraud-Detector/Call_Processing/releases/latest/download/app-release.apk)

<!-- [<img src="https://raw.githubusercontent.com/ImranR98/Obtainium/refs/heads/main/assets/graphics/badge_obtainium.png"
     alt="Get it on Obtainium"
     height="80">](https://apt.izzysoft.de/fdroid/index/apk/dev.imranr.obtainium) -->

## Features

- Full call handling (incoming, outgoing, ongoing) with Jetpack Compose UI
- Multi-SIM support with SIM selection dialog
- Call logs with Paging 3, contacts integration, and favorites
- DTMF dialpad with tone muting
- Call blocking and auto-answer (configurable 1.5s delay)
- App settings shortcut on onboarding screen

### New in this fork

- **Authentication** — email/password login and registration against a remote API with token persistence via DataStore
- **Session-aware startup** — validates stored tokens on launch; expired sessions redirect to login with a clear message
- **FCM remote hangup** — registers device FCM token with the backend and listens for a remote "hangup" command to forcibly terminate active calls
- **Fraud reporting** — incoming call phone numbers are sent to a fraud analysis API for scam detection
- **CI/CD pipeline** — GitHub Actions workflow builds a signed release APK on every push and creates a GitHub Release on master
- **Auto-answer toggle** — automatically answer incoming calls after a configurable delay (default 1.5s)
- **English localization** — all UI text translated from Chinese

## Tech Stack

- **Kotlin** 2.x, Jetpack Compose (BOM 2026), Navigation 3, Hilt DI, Paging 3
- **Retrofit** + **OkHttp** for networking
- **Firebase Cloud Messaging** for push commands
- **DataStore Preferences** for token/settings persistence
- **Minimum SDK:** 26 (Android 8.0) / **Target SDK:** 36 (Android 16)

## Build

```bash
# Debug
./gradlew assembleDebug

# Release (requires signing config)
./gradlew assembleRelease
```

The CI pipeline in `.github/workflows/build-release.yml` handles signing via GitHub Secrets (`SIGNATURE_JSON`, `GOOGLE_SERVICES_JSON`).

## Backend API

Configured in `ApiConfig.kt` — points to `https://vision.futuremedialab.tw:1688/`. The API provides:

- `POST /api/auth/login` — authenticate
- `POST /api/auth/register` — create account
- `GET /api/auth/status` — validate session
- `POST /api/fraud/incoming-call` — report incoming call for fraud analysis
- `POST /api/push/subscribe/host_mobile` — register FCM token

## License

Original project by [amadz](https://github.com/amadz). This fork maintains the same open-source terms.
