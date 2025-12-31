# FlowNotes 📝

A simple, offline-first note-taking app for Android built with Jetpack Compose, designed for privacy and ease of use.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.0-green.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## ✨ Features

### Core Functionality
- **📝 Rich Text Editing**: Format your notes with bold, italic, underline, and more
- **📌 Pin Notes**: Keep important notes at the top
- **🔍 Smart Search**: Find notes quickly with debounced search (300ms)
- **🏷️ Tags & Categories**: Organize notes with tags and predefined categories
- **🔔 Reminders**: Set notifications for important notes
- **🎨 Custom Colors**: Personalize notes with different colors
- **💾 Auto-Save**: Changes saved automatically every 500ms

### User Experience
- **📱 Offline-First**: All data stored locally, no internet required
- **🌓 Dark Mode**: Full support for light and dark themes
- **📲 Tablet Support**: Responsive layouts for tablets (centered content, adaptive fonts)
- **↔️ Swipe to Delete**: Quick gesture-based deletion with confirmation
- **🔒 Privacy Focused**: No cloud sync, no data collection, no tracking

### Advanced Features
- **🎯 Input Validation**: Title max 200 chars, tag validation
- **⚡ Performance Optimized**: Pagination (50 notes), debounced search
- **🛡️ Error Handling**: Comprehensive error handling with user feedback
- **📊 Contextual Empty States**: Helpful messages based on user context

## 🏗️ Architecture

FlowNotes follows **Clean Architecture** principles with MVVM pattern:

```
app/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── model/          # Domain models
│   └── repository/     # Repository pattern
├── di/                 # Hilt dependency injection
├── ui/
│   ├── components/     # Reusable UI components
│   ├── navigation/     # Navigation setup
│   ├── screens/        # Feature screens (Home, Editor, Settings)
│   └── theme/          # Material Design 3 theming
└── util/               # Utilities (notifications, window size)
```

### Tech Stack

**Core:**
- Kotlin 1.9.0
- Jetpack Compose (Material Design 3)
- Coroutines & Flow

**Architecture:**
- MVVM Pattern
- Clean Architecture
- Repository Pattern
- Hilt (Dependency Injection)

**Data:**
- Room Database
- DataStore (Preferences)
- Gson (JSON serialization)

**UI:**
- Material Design 3
- Compose Navigation
- Rich Text Editor (MohamedRejeb)

**Other:**
- AlarmManager (Reminders)
- WorkManager (Background tasks)

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK 34
- Minimum SDK: 26 (Android 8.0)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/flownotes.git
   cd flownotes
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift + F10`

### Build Variants

- **Debug**: Development build with debugging enabled
- **Release**: Production build with ProGuard/R8 optimization

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

## 📱 Screenshots

*Coming soon - Add screenshots of your app here*

## 🎯 Roadmap

### v0.1.0 (Current)
- ✅ Core note-taking functionality
- ✅ Rich text editing
- ✅ Categories and tags
- ✅ Search and filters
- ✅ Reminders
- ✅ Tablet support
- ✅ Error handling
- ✅ Help system

### v0.2.0 (Planned)
- [ ] Multi-column grid for tablets
- [ ] Master-detail layout for tablets
- [ ] Backup/restore improvements
- [ ] Note templates
- [ ] Export to PDF/Markdown
- [ ] Widget support
- [ ] Checklist improvements

### v1.0.0 (Future)
- [ ] Note sharing
- [ ] Attachments support
- [ ] Voice notes
- [ ] Handwriting support
- [ ] Advanced search filters

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit your changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to the branch** (`git push origin feature/AmazingFeature`)
5. **Open a Pull Request**

### Code Style
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [MohamedRejeb](https://github.com/MohamedRejeb) for the Rich Text Editor library
- [Material Design 3](https://m3.material.io/) for design guidelines
- [Jetpack Compose](https://developer.android.com/jetpack/compose) team

## 📞 Contact

- **Website**: [flownotes-presencematic.netlify.app](https://flownotes-presencematic.netlify.app)
- **Email**: [Contact through website](https://flownotes-presencematic.netlify.app/contact)

## 🔒 Privacy

FlowNotes is **100% offline** and **privacy-focused**:
- ✅ No internet connection required
- ✅ No cloud sync
- ✅ No data collection
- ✅ No analytics
- ✅ No ads
- ✅ All data stored locally on your device

Your notes are **yours** and **yours alone**.

---

**Made with ❤️ by the FlowNotes Team**
