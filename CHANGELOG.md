# Changelog

All notable changes to FlowNotes will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned for v0.2.0
- Multi-column note grid for tablets
- Master-detail layout for large screens
- Note templates
- Export to PDF/Markdown
- Improved backup/restore
- Widget support

## [0.1.0] - 2025-12-31

### Added
- **Core Features**
  - Rich text editing with formatting (bold, italic, underline, etc.)
  - Create, edit, and delete notes
  - Pin important notes to the top
  - Search notes with 300ms debounce
  - Categories (General, Tasks, Ideas, Meetings, Code, Recipes, Study)
  - Tags for organization
  - Color customization for notes
  - Reminder notifications with AlarmManager
  
- **User Experience**
  - Auto-save with 500ms debounce
  - Swipe-to-delete with confirmation dialog
  - Contextual empty states (search, category, tags)
  - Dark mode support
  - Offline-first architecture
  - Help & Guide screen
  
- **Performance**
  - Pagination (50 notes per load)
  - Debounced search queries
  - Optimized database queries with LIMIT
  
- **Tablet Support**
  - Responsive layouts with WindowSizeClass
  - Max width constraint for editor (720dp/840dp)
  - Adaptive font sizes (1.0x/1.05x/1.1x)
  - Adaptive spacing
  
- **Error Handling**
  - Try-catch blocks in all ViewModels
  - Snackbar error notifications
  - User-friendly error messages
  
- **Input Validation**
  - Title max 200 characters with counter
  - Tag validation (max 50 chars, no duplicates, trimmed)
  - Empty tag prevention
  
- **UI Improvements**
  - Bottom nav label: "Notes" → "Home"
  - All pinned notes shown in horizontal scroll
  - "See all pinned (X)" button
  - Offline-first indicator in Settings
  - Delete confirmation dialogs
  
- **String Resources**
  - All hardcoded strings moved to strings.xml
  - Support for localization
  
- **Documentation**
  - Comprehensive README.md
  - Help & Guide screen
  - Code comments and documentation

### Changed
- Auto-save debounce reduced from 1000ms to 500ms
- Search debounce set to 300ms
- Pinned notes now show all instead of just 3

### Fixed
- Navigation issues and dead code removed
- Duplicate code eliminated
- Proper error handling throughout app
- Input validation edge cases

### Technical
- **Architecture**: MVVM with Clean Architecture
- **Database**: Room with optimized queries
- **DI**: Hilt for dependency injection
- **UI**: Jetpack Compose with Material Design 3
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## [0.0.1] - Initial Development

### Added
- Basic note-taking functionality
- Room database setup
- Jetpack Compose UI
- Navigation structure
- Hilt dependency injection

---

## Version History Summary

- **v0.1.0** (2025-12-31): First stable release with all core features
- **v0.0.1** (Development): Initial project setup

## Upgrade Notes

### From 0.0.1 to 0.1.0
- Database schema unchanged - no migration needed
- All features are additive - no breaking changes
- Settings and preferences preserved
