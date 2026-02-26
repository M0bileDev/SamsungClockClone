# 🕐 Samsung Clock Clone

A feature-rich Android clock application inspired by the **Samsung Clock** app, built entirely with Kotlin and modern Android development practices. This project serves as a deep-dive into building polished, production-quality UI with Jetpack Compose while replicating the familiar and intuitive Samsung Clock experience.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🕰️ **Clock** | World clock support with multiple timezones |
| ⏰ **Alarm** | Create, edit, and manage alarms with repeat schedules |
| ⏱️ **Stopwatch** | Lap tracking with a smooth, animated interface |
| ⏳ **Timer** | Countdown timer with custom duration and notifications |

---

## 🏗️ Architecture

The app follows **MVVM + Clean Architecture**, keeping UI, business logic, and data concerns clearly separated.

```
app/
├── ui/             # Compose screens, ViewModels, navigation
│   ├── clock/
│   ├── alarm/
│   ├── stopwatch/
│   └── timer/
├── domain/         # Use cases and business logic
└── data/           # Local data sources (Room), repositories
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin 100% |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **Async** | Kotlin Coroutines + Flow |
| **Dependency Injection** | Hilt |
| **Local Storage** | Room Database |
| **Navigation** | Jetpack Navigation (Compose) |
| **Notifications** | Android AlarmManager + NotificationManager |
| **Build System** | Gradle (Kotlin DSL) |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17+
- Android SDK 26+

### Build & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/M0bileDev/SamsungClockClone.git
   cd SamsungClockClone
   ```

2. Open in Android Studio and sync Gradle.

3. Run on an emulator or physical device (API 26+).

> **Note:** Alarm and Timer functionality requires the `SCHEDULE_EXACT_ALARM` permission, which may need to be manually granted on Android 12+ devices.

---

## 🎨 Design Inspiration

This project is a UI/UX study inspired by the **Samsung Clock** app. The goal is to faithfully recreate the look, feel, and behaviour of Samsung's clock experience using **only** the Android Jetpack toolkit — no Samsung-specific SDKs required. Key design considerations include:

- Tab-based navigation mirroring the Samsung Clock layout
- Smooth animations for stopwatch and timer transitions
- Consistent use of Material 3 typography and colour tokens
- Dark mode support

---

## 🔔 Permissions

| Permission | Purpose |
|---|---|
| `RECEIVE_BOOT_COMPLETED` | Restore alarms after device reboot |
| `SCHEDULE_EXACT_ALARM` | Fire alarms at precise times (Android 12+) |
| `POST_NOTIFICATIONS` | Show alarm and timer notifications (Android 13+) |
| `USE_EXACT_ALARM` | Background timer and alarm execution |
| `VIBRATE` | Alarm and timer haptic feedback |

---

## 📄 License

```
Copyright 2024 M0bileDev

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

> **Disclaimer:** This project is an independent study and is not affiliated with, endorsed by, or connected to Samsung Electronics in any way. All design inspiration is based on publicly available Samsung Clock UI.
