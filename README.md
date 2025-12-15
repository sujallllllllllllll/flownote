# FlowNote - Native Android Note-Taking App

**Capture thoughts faster than you think**

FlowNote is a native Android app built with Kotlin and Jetpack Compose, focused on ultra-fast note capture and intelligent organization.

## Features (MVP)

### Sprint 1 - Core Foundation ✅
- ✅ Native Android with Kotlin + Jetpack Compose
- ✅ Room database (100% offline, local storage)
- ✅ Material Design 3 with dark mode support
- ✅ Basic note CRUD operations
- 🚧 Note list view with empty state

### Sprint 2 - Essential Features (Upcoming)
- Search functionality
- Manual categories/tags
- Note pinning
- Swipe to delete
- Settings screen

### Sprint 3 - Intelligence Layer (Upcoming)
- Voice recording
- Auto-categorization
- Fuzzy search
- Temporary notes system

### Sprint 4 - Polish (Upcoming)
- Animations and transitions
- Home screen widget
- Error handling
- Performance optimization

## Tech Stack

- **Language**: Kotlin 1.9.22
- **UI**: Jetpack Compose with Material Design 3
- **Database**: Room (SQLite)
- **DI**: Hilt
- **Architecture**: MVVM + Clean Architecture
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/src/main/java/com/flownote/
├── data/                    # Data layer
│   ├── local/              # Local database
│   │   ├── dao/           # Data Access Objects
│   │   ├── database/      # Room database
│   │   └── entity/        # Database entities
│   ├── model/             # Domain models
│   └── repository/        # Repositories
├── di/                     # Dependency injection
├── ui/                     # Presentation layer
│   ├── navigation/        # Navigation
│   ├── screens/           # Screens
│   └── theme/             # Material Design theme
└── util/                   # Utilities
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Gradle 8.2+

### Build & Run

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or physical device (Android 8.0+)

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## Key Features

### 100% Offline-First
- All data stored locally on device
- No backend server required
- No authentication needed
- Works in airplane mode
- Privacy-focused (data never leaves device)

### Fast & Lightweight
- App launch: < 2 seconds
- Note creation: < 200ms
- Smooth 60fps scrolling
- Target app size: < 25 MB

### Auto-Categorization (Coming in Sprint 3)
- Meetings: Time indicators + meeting keywords
- Tasks: Action verbs + list format
- Recipes: Recipe keywords + ingredients
- Code Snippets: Code syntax detection
- Ideas: Questions + future tense
- Study Notes: Academic keywords
- General: Default fallback

## Development Timeline

| Sprint | Duration | Status |
|--------|----------|--------|
| Sprint 1 | Weeks 1-2 | 🚧 In Progress |
| Sprint 2 | Weeks 3-4 | ⏳ Planned |
| Sprint 3 | Weeks 5-6 | ⏳ Planned |
| Sprint 4 | Week 7 | ⏳ Planned |

## License

This project is currently in development.

## Contact

For questions or feedback, please open an issue on GitHub.
