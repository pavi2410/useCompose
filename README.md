# useCompose

Headless @Composable hooks that drive UI logic. Inspired by React.

**🚀 Now supports Kotlin/Compose Multiplatform!** Run on Android, Desktop, and more!

> **📢 Looking for other hooks?** In v2.0, we focused on KMP support and kept only the `query` module. For other hooks like `useState`, `useEffect`, `useContext`, `useReducer`, `useToggle`, and `useConnectionStatus`, check out [**ComposeHooks**](https://github.com/junerver/ComposeHooks) - a comprehensive collection of Compose hooks!

[![](https://jitpack.io/v/pavi2410/useCompose.svg)](https://jitpack.io/#pavi2410/useCompose) [![CI](https://github.com/pavi2410/useCompose/actions/workflows/ci.yml/badge.svg)](https://github.com/pavi2410/useCompose/actions/workflows/ci.yml)

| ❓ `query` |
| --- |
| ![query example](https://github.com/pavi2410/useCompose/assets/28837746/2317d447-8f8b-4626-b92e-2e024e242714) |

## Quick Start

### Run the Demo

**Desktop App:**
```bash
./gradlew :app:run
```

**Android App:**
```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

**Run Tests:**
```bash
# All tests
./gradlew test

# Library tests only
./gradlew :query:test

# App tests only
./gradlew :app:test
```

## Modules

### ❓ query
- `useQuery` - Async data fetching with loading/error/success states
- `useMutation` - Async mutations with callback handling

**Platforms supported:** Android, Desktop (JVM), ready for iOS/Web

## Installation

### For Kotlin/Compose Multiplatform Projects

Add to your `libs.versions.toml`:
```toml
[versions]
useCompose = "2.0.0"  # Use latest version

[repositories]
maven { url = "https://jitpack.io" }

[libraries]
useCompose-query = { module = "com.github.pavi2410.useCompose:query", version.ref = "useCompose" }
```

Add to your module's `build.gradle.kts`:
```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.useCompose.query)
        }
    }
}
```

### For Android-only Projects

Add to your `build.gradle.kts`:
```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.pavi2410.useCompose:query:2.0.0")
}
```

## Example Usage

```kotlin
@Composable
fun MyScreen() {
    val queryState by useQuery {
        // Async operation
        fetchDataFromApi()
    }

    when (val state = queryState) {
        is QueryState.Loading -> Text("Loading...")
        is QueryState.Error -> Text("Error: ${state.message}")
        is QueryState.Content -> Text("Data: ${state.data}")
    }
}
```

## Help Wanted

This library now supports Kotlin Multiplatform! Help us extend it with more hooks and platform support (iOS, Web, etc.).

## License

[MIT](https://choosealicense.com/licenses/mit/)
