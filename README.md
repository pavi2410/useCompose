# useCompose

Headless @Composable hooks that drive UI logic. Inspired by React.

**🚀 Now supports Kotlin/Compose Multiplatform!** Run on Android, Desktop, and more!

> **📢 Looking for other hooks?** In v2.0, we focused on KMP support and kept only the `query` module. For other hooks like `useState`, `useEffect`, `useContext`, `useReducer`, `useToggle`, and `useConnectionStatus`, check out [**ComposeHooks**](https://github.com/junerver/ComposeHooks) - a comprehensive collection of Compose hooks!

[![](https://jitpack.io/v/pavi2410/useCompose.svg)](https://jitpack.io/#pavi2410/useCompose) [![CI](https://github.com/pavi2410/useCompose/actions/workflows/ci.yml/badge.svg)](https://github.com/pavi2410/useCompose/actions/workflows/ci.yml)

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
- `useQuery` - Type-safe async data fetching with caching and loading/error/success states
- `useMutation` - Async mutations with callback handling
- **Type-safe Keys** - Strongly-typed query keys using data classes
- **QueryClient** - Centralized cache management with automatic deduplication
- **Cache Invalidation** - Type-safe cache invalidation patterns

**Platforms supported:** Android, Desktop (JVM), ready for iOS/Web

## Installation

Add to your `libs.versions.toml`:
```toml
[versions]
useCompose = "2.0.0"  # Use latest version

[libraries]
useCompose-query = { module = "com.github.pavi2410.useCompose:query", version.ref = "useCompose" }
```

Add to your project's `build.gradle.kts` (project level):
```kotlin
allprojects {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add to your module's `build.gradle.kts`:

**For Kotlin Multiplatform:**
```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.useCompose.query)
        }
    }
}
```

**For Android:**
```kotlin
dependencies {
    implementation(libs.useCompose.query)
}
```

## Getting Started

### 1. Set up QueryClient

First, create a `QueryClient` and wrap your app with `QueryClientProvider`:

```kotlin
@Composable
fun App() {
    QueryClientProvider(client = remember { QueryClient() }) {
        // Your app content
        MyAppContent()
    }
}
```

### 2. Define Type-Safe Keys

Create data classes that implement the `Key` interface:

```kotlin
data class UserKey(val userId: Long) : Key
data class PostsKey(val userId: Long, val page: Int = 1) : Key
data class RepoKey(val owner: String, val name: String) : Key

// For singleton keys without parameters, use data object
data object AllUsersKey : Key
data object AppConfigKey : Key
```

### 3. Use Queries

Use the `useQuery` hook with your type-safe keys:

```kotlin
@Composable
fun UserProfile(userId: Long) {
    val queryState by useQuery(
        key = UserKey(userId),
        queryFn = {
            // Your async operation
            fetchUserFromApi(userId)
        }
    )

    when (val state = queryState.dataState) {
        is DataState.Pending -> Text("Loading...")
        is DataState.Error -> Text("Error: ${state.message}")
        is DataState.Success -> Text("User: ${state.data.name}")
    }
}
```

## Features

- **🔒 Type Safety**: Query keys are strongly-typed data classes, preventing typos and enabling IDE support
- **🚀 Automatic Caching**: Queries are cached automatically and shared across components
- **♻️ Smart Invalidation**: Type-safe cache invalidation with `invalidateQuery()` and `invalidateQueriesOfType<T>()`
- **⚡ Request Deduplication**: Multiple components requesting the same data share a single network request
- **🎯 Compose Integration**: Built specifically for Jetpack Compose with reactive state updates

## Cache Management

### Invalidate Specific Query
```kotlin
val queryClient = useQueryClient()

// Invalidate a specific query
queryClient.invalidateQuery(UserKey(123))
```

### Invalidate by Type
```kotlin
// Invalidate all user queries
queryClient.invalidateQueriesOfType<UserKey>()
```

## Example Usage

### Basic Query with HTTP API

```kotlin
data class RepoKey(val repoPath: String) : Key

@Composable
fun GitHubRepoExample() {
    val queryState by useQuery(
        key = RepoKey("pavi2410/useCompose"),
        queryFn = {
            // Your HTTP client call
            httpClient.get("https://api.github.com/repos/pavi2410/useCompose")
                .body<RepoData>()
        }
    )

    when (val state = queryState.dataState) {
        is DataState.Pending -> Text("Loading repository...")
        is DataState.Error -> Text("Error: ${state.message}")
        is DataState.Success -> {
            val repo = state.data
            Column {
                Text("Name: ${repo.full_name}")
                Text("Stars: ${repo.stargazers_count}")
            }
        }
    }
}
```

### Multiple Queries with Different Keys

```kotlin
@Composable
fun PostsAndCommentsExample(postId: Int) {
    // Each query is cached independently
    val postsQuery by useQuery(
        key = PostsListKey(),
        queryFn = { fetchAllPosts() }
    )

    val postDetailQuery by useQuery(
        key = PostDetailKey(postId),
        queryFn = { fetchPost(postId) }
    )

    // UI renders both queries...
}
```

## API Reference

### Key Interface

The foundation of type-safe queries. Implement this interface on data classes:

```kotlin
interface Key

// Examples:
data class UserKey(val userId: Long) : Key
data class PostKey(val postId: String) : Key
object AllPostsKey : Key  // For singleton keys
```

### useQuery

The main hook for data fetching with caching:

```kotlin
@Composable
fun <T> useQuery(
    key: Key,
    queryFn: suspend CoroutineScope.() -> T,
    options: QueryOptions = QueryOptions.Default
): State<QueryState<T>>
```

**Parameters:**
- `key` - Type-safe key for caching and identification
- `queryFn` - Suspend function that fetches the data
- `options` - Configuration options (e.g., `enabled`)

### QueryState

The state returned by `useQuery`:

```kotlin
data class QueryState<T>(
    val fetchStatus: FetchStatus,  // Idle, Fetching
    val dataState: DataState<T>    // Pending, Error, Success
)

sealed interface DataState<out T> {
    object Pending : DataState<Nothing>
    data class Error(val message: String) : DataState<Nothing>
    data class Success<T>(val data: T) : DataState<T>
}
```

### QueryClient

Central cache management:

```kotlin
class QueryClient {
    suspend fun <T> getQuery(key: Key, queryFn: suspend CoroutineScope.() -> T): CacheEntry<T>
    suspend fun invalidateQuery(key: Key)
    suspend inline fun <reified T : Key> invalidateQueriesOfType()
    suspend fun clear()
}
```

### QueryClientProvider

Compose provider for dependency injection:

```kotlin
@Composable
fun QueryClientProvider(
    client: QueryClient,
    content: @Composable () -> Unit
)

@Composable
fun useQueryClient(): QueryClient
```

## Help Wanted

This library now supports Kotlin Multiplatform! Help us extend it with more hooks and platform support (iOS, Web, etc.).

## License

[MIT](https://choosealicense.com/licenses/mit/)
