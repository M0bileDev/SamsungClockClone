# Samsung Clock Clone — Alarm ⏰

An Android application inspired by Samsung's Clock app, built with modern Android development tools. The project focuses on the **Alarm** feature — recreating the look, feel, and functionality of Samsung's alarm experience using Jetpack Compose and Clean Architecture.

---

## ✨ Features

- 🔔 **Create & manage alarms** — add, edit, and delete alarms with ease
- 🔁 **Recurring alarms** — set alarms to repeat on selected days of the week
- 🔇 **Enable / disable alarms** — toggle individual alarms on or off
- 🏷️ **Alarm labels** — name your alarms for quick identification
- 📳 **Snooze & dismiss** — full alarm ringing screen with snooze and dismiss actions
- 🎨 **Samsung-inspired UI** — faithful recreation of Samsung's Clock alarm design using Material 3 and Jetpack Compose

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Declarative UI |
| **Material 3** | Design system & theming |
| **MVVM + Clean Architecture** | Presentation / Domain / Data separation |
| **Hilt** | Dependency injection |
| **Room** | Local alarm persistence |
| **AlarmManager** | Scheduling exact alarms |
| **BroadcastReceiver / Service** | Alarm triggering & ringing |
| **Kotlin Coroutines & Flow** | Async operations & reactive state |

---

## 🏗️ Architecture

The project follows **MVVM Clean Architecture** with a clear separation of concerns across three layers:

```
app/
├── data/           # Room database, DAOs, repositories implementation
├── domain/         # Use cases, domain models, repository interfaces
└── presentation/   # Compose UI, ViewModels, navigation
```

A single-activity setup with Compose Navigation handles all screen transitions.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Android SDK 26+

### Run the app

1. Clone the repository:
   ```bash
   git clone https://github.com/M0bileDev/SamsungClockClone.git
   ```

2. Open the project in **Android Studio**.

3. Sync Gradle and run on an emulator or physical device:
   ```bash
   ./gradlew :app:installDebug
   ```

> ⚠️ Exact alarm scheduling on **Android 12+** requires the `SCHEDULE_EXACT_ALARM` permission. The app will prompt the user to grant it if not already enabled.

---

## 📋 Requirements

- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 35
- **Language:** Kotlin

---

## 📄 License

```
Apache License 2.0
```

Feel free to use, modify, and distribute with attribution.

> ⚠️ **Disclaimer:** This project was created purely for **educational purposes** as a learning exercise in modern Android development. It is not affiliated with, endorsed by, or associated with **Samsung Electronics Co., Ltd.** in any way. All Samsung product names and trademarks are the property of their respective owners.
