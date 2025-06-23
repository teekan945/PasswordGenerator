# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android password generator app built with Kotlin and Jetpack Compose. The app allows users to generate secure passwords with customizable length and character types (uppercase, lowercase, numbers, symbols). It includes password strength evaluation using the zxcvbn library and clipboard functionality.

## Common Commands

### Build & Run
```bash
./gradlew build                    # Build the project
./gradlew assembleDebug           # Build debug APK
./gradlew assembleRelease         # Build release APK
./gradlew installDebug            # Install debug APK to connected device
```

### Testing
```bash
./gradlew test                    # Run unit tests
./gradlew connectedAndroidTest    # Run instrumented tests on device/emulator
./gradlew testDebugUnitTest       # Run debug unit tests specifically
```

### Lint & Code Quality
```bash
./gradlew lint                    # Run lint checks
./gradlew lintDebug              # Run lint on debug build
```

### Clean
```bash
./gradlew clean                   # Clean build artifacts
```

## Architecture

### Package Structure
- `com.tolulonge.passwordgenerator`
  - `data/` - Data models and state classes
  - `ui/components/` - Composable UI components
  - `ui/theme/` - Material3 theme configuration
  - `utils/` - Utility classes and business logic

### Key Components

**State Management**: Uses Compose's `mutableStateOf` with `PasswordState` data class for UI state management.

**Password Generation**: `PasswordGenerator` object handles password creation logic with configurable character sets.

**Password Strength**: Uses the zxcvbn library (Nulab implementation) for password strength evaluation in `PasswordStrengthIndicator`.

**UI Layer**: Single-activity architecture with `MainActivity` hosting a `PasswordGeneratorScreen` composable.

### Dependencies
- Jetpack Compose with Material3 design system
- zxcvbn library for password strength analysis
- Standard Android libraries (core-ktx, lifecycle, activity-compose)

### Build Configuration
- Target SDK: 35, Min SDK: 24
- Kotlin version: 2.0.21
- Uses Gradle version catalogs for dependency management
- Compose compiler enabled via kotlin-compose plugin