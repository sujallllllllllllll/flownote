# Contributing to FlowNotes

Thank you for your interest in contributing to FlowNotes! This document provides guidelines and instructions for contributing.

## 🎯 Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on what is best for the community
- Show empathy towards other community members

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Git
- Basic knowledge of Kotlin and Jetpack Compose

### Setup Development Environment

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/flownotes.git
   cd flownotes
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/flownotes.git
   ```
4. **Open in Android Studio** and sync Gradle

## 📝 How to Contribute

### Reporting Bugs

Before creating a bug report:
- Check if the bug has already been reported
- Collect information about the bug (Android version, device, steps to reproduce)

**Bug Report Template:**
```markdown
**Describe the bug**
A clear description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. See error

**Expected behavior**
What you expected to happen.

**Screenshots**
If applicable, add screenshots.

**Device Info:**
 - Device: [e.g. Pixel 6]
 - Android Version: [e.g. 14]
 - App Version: [e.g. 0.1.0]
```

### Suggesting Features

**Feature Request Template:**
```markdown
**Problem Statement**
Describe the problem you're trying to solve.

**Proposed Solution**
Describe your proposed solution.

**Alternatives Considered**
What other solutions did you consider?

**Additional Context**
Any other context or screenshots.
```

### Pull Requests

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/amazing-feature
   ```

2. **Make your changes** following our coding standards

3. **Test your changes**:
   - Run the app on multiple devices/emulators
   - Test edge cases
   - Ensure no regressions

4. **Commit your changes**:
   ```bash
   git commit -m "feat: add amazing feature"
   ```
   
   Follow [Conventional Commits](https://www.conventionalcommits.org/):
   - `feat:` New feature
   - `fix:` Bug fix
   - `docs:` Documentation changes
   - `style:` Code style changes (formatting)
   - `refactor:` Code refactoring
   - `perf:` Performance improvements
   - `test:` Adding tests
   - `chore:` Maintenance tasks

5. **Push to your fork**:
   ```bash
   git push origin feature/amazing-feature
   ```

6. **Open a Pull Request** on GitHub

## 💻 Coding Standards

### Kotlin Style Guide

Follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// ✅ Good
fun calculateTotal(items: List<Item>): Double {
    return items.sumOf { it.price }
}

// ❌ Bad
fun calc(i: List<Item>): Double {
    var t = 0.0
    for (item in i) t += item.price
    return t
}
```

### Compose Best Practices

```kotlin
// ✅ Good - Stateless composable
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.clickable(onClick = onClick)) {
        Text(note.title)
    }
}

// ❌ Bad - Stateful composable doing too much
@Composable
fun NoteCard(viewModel: NoteViewModel) {
    val note by viewModel.note.collectAsState()
    // ...
}
```

### Architecture Guidelines

1. **MVVM Pattern**
   - ViewModels handle business logic
   - Composables are stateless when possible
   - Use StateFlow for state management

2. **Repository Pattern**
   - Repositories abstract data sources
   - Single source of truth
   - Handle data transformations

3. **Dependency Injection**
   - Use Hilt for DI
   - Inject dependencies, don't create them
   - Use constructor injection

### File Organization

```
feature/
├── FeatureScreen.kt        # UI composables
├── FeatureViewModel.kt     # Business logic
└── components/             # Feature-specific components
    └── FeatureCard.kt
```

### Naming Conventions

- **Classes**: PascalCase (`NoteRepository`)
- **Functions**: camelCase (`saveNote()`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_TITLE_LENGTH`)
- **Composables**: PascalCase (`NoteCard`)
- **Resources**: snake_case (`string_note_title`)

## 🧪 Testing

### Running Tests

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Writing Tests

```kotlin
@Test
fun `saveNote should update database`() = runTest {
    // Given
    val note = Note(title = "Test")
    
    // When
    repository.saveNote(note)
    
    // Then
    val saved = repository.getNoteById(note.id)
    assertEquals(note.title, saved?.title)
}
```

## 📚 Documentation

- Add KDoc comments for public APIs
- Update README.md for new features
- Add inline comments for complex logic
- Update CHANGELOG.md

```kotlin
/**
 * Saves a note to the database.
 *
 * @param note The note to save
 * @throws DatabaseException if save fails
 */
suspend fun saveNote(note: Note)
```

## 🔍 Code Review Process

1. **Self-review** your code before submitting
2. **Respond to feedback** promptly and professionally
3. **Make requested changes** or discuss alternatives
4. **Keep PRs focused** - one feature/fix per PR
5. **Update PR** if main branch changes

## 📋 Checklist Before Submitting PR

- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings or errors
- [ ] Tested on multiple devices/screen sizes
- [ ] CHANGELOG.md updated (if applicable)
- [ ] Commit messages follow conventions

## 🎨 UI/UX Guidelines

- Follow Material Design 3 guidelines
- Support both light and dark themes
- Ensure accessibility (content descriptions, contrast)
- Test on different screen sizes
- Use adaptive layouts for tablets

## 🐛 Debugging Tips

- Use Android Studio's debugger
- Check Logcat for errors
- Use Layout Inspector for UI issues
- Profile performance with Android Profiler

## 📞 Getting Help

- Open a GitHub Discussion for questions
- Join our community (if applicable)
- Check existing issues and PRs
- Read the documentation

## 🙏 Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes
- GitHub contributors page

Thank you for contributing to FlowNotes! 🎉
