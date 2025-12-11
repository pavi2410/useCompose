# Query DSL API Design

## Overview

This document outlines the API design for a Query Definition DSL that provides clean separation of data fetching logic from UI components while maintaining all the power of React Query/TanStack Query.

## Core Principles

1. **Separation of Concerns** - Data fetching logic lives outside UI components
2. **Type Safety** - Strongly typed keys, query functions, and return types
3. **Developer Experience** - Natural Kotlin DSL with property setters
4. **Feature Parity** - Support all React Query features (caching, refetching, invalidation, optimistic updates)

## Query Definition API

### Basic Query Definition

```kotlin
// Define a query using the DSL
val getAllPosts = query<List<Post>> {
    key = PostsListKey()
    queryFn = {
        httpClient.get("/api/posts").body()
    }
    staleTime = 5.minutes
    cacheTime = 10.minutes
    refetchOnWindowFocus = true
    refetchOnReconnect = true
    retry = RetryConfig.Default
}

// Use in a Composable
@Composable
fun PostsList() {
    val queryState by useQuery(getAllPosts)

    when (val state = queryState.dataState) {
        is DataState.Pending -> LoadingIndicator()
        is DataState.Error -> ErrorMessage(state.message)
        is DataState.Success -> PostList(state.data)
    }
}
```

### Dynamic Query Definition

```kotlin
// Query with parameters
fun getPostById(postId: Int) = query<Post> {
    key = PostDetailKey(postId)
    queryFn = {
        httpClient.get("/api/posts/$postId").body()
    }
    staleTime = 30.seconds
    cacheTime = 5.minutes
}

// Use in a Composable
@Composable
fun PostDetail(postId: Int) {
    val queryState by useQuery(getPostById(postId))
    // ...
}
```

### Query Builder Properties

```kotlin
class QueryBuilder<T> {
    // Required properties
    lateinit var key: Key
    lateinit var queryFn: suspend CoroutineScope.() -> T

    // Optional properties with defaults
    var enabled: Boolean = true
    var staleTime: Duration = Duration.ZERO
    var cacheTime: Duration = 5.minutes
    var refetchInterval: Duration? = null
    var refetchOnWindowFocus: Boolean = true
    var refetchOnReconnect: Boolean = true
    var refetchOnMount: Boolean = true
    var retry: RetryConfig = RetryConfig.Default
    var suspense: Boolean = false
    var keepPreviousData: Boolean = false
    var structuralSharing: Boolean = true
}
```

## Mutation Definition API

### Basic Mutation

```kotlin
val createPost = mutation<Post, CreatePostRequest> {
    mutationFn = { request ->
        httpClient.post("/api/posts") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    retry = RetryConfig(maxAttempts = 2)
}

// Use in a Composable
@Composable
fun CreatePostForm() {
    val mutation = useMutation(createPost)

    Button(
        onClick = {
            mutation.mutate(
                CreatePostRequest(title = "New Post", body = "Content")
            )
        },
        enabled = !mutation.isLoading
    ) {
        Text("Create Post")
    }
}
```

### Mutation with Optimistic Updates

```kotlin
val updatePost = mutation<Post, UpdatePostRequest, PreviousData> {
    mutationFn = { request ->
        httpClient.put("/api/posts/${request.id}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    onMutate = { request ->
        // Cancel any outgoing refetches
        queryClient.cancelQueries(PostDetailKey(request.id))

        // Snapshot the previous value
        val previousPost = queryClient.getQueryData<Post>(PostDetailKey(request.id))

        // Optimistically update to the new value
        queryClient.setQueryData(PostDetailKey(request.id), request.toPost())

        // Return context for rollback
        PreviousData(previousPost)
    }

    onError = { error, request, context ->
        // Rollback on error
        context?.previousPost?.let {
            queryClient.setQueryData(PostDetailKey(request.id), it)
        }
    }

    onSuccess = { data, request, context ->
        // Update with server response
        queryClient.setQueryData(PostDetailKey(request.id), data)
        queryClient.invalidateQueries(PostsListKey())
    }

    onSettled = { data, error, request, context ->
        // Always refetch after error or success
        queryClient.invalidateQueries(PostDetailKey(request.id))
    }
}
```

### Mutation Builder Properties

```kotlin
class MutationBuilder<TData, TVariables, TContext> {
    // Required property
    lateinit var mutationFn: suspend CoroutineScope.(variables: TVariables) -> TData

    // Optional callbacks
    var onMutate: (suspend (variables: TVariables) -> TContext?)? = null
    var onSuccess: (suspend (data: TData, variables: TVariables, context: TContext?) -> Unit)? = null
    var onError: (suspend (error: Throwable, variables: TVariables, context: TContext?) -> Unit)? = null
    var onSettled: (suspend (data: TData?, error: Throwable?, variables: TVariables, context: TContext?) -> Unit)? = null

    // Options
    var retry: RetryConfig = RetryConfig.Default
    var throwOnError: Boolean = false
}
```

## Advanced Patterns

### Dependent Queries

```kotlin
// User profile query
val getUserProfile = query<User> {
    key = UserKey(userId)
    queryFn = {
        httpClient.get("/api/users/$userId").body()
    }
    staleTime = 10.minutes
}

// Posts query that depends on user
fun getUserPosts(userId: Int?) = query<List<Post>> {
    key = UserPostsKey(userId ?: -1)
    queryFn = {
        httpClient.get("/api/users/$userId/posts").body()
    }
    enabled = userId != null  // Only run when userId is available
    staleTime = 2.minutes
}

// Usage
@Composable
fun UserDashboard(userId: Int) {
    val userQuery by useQuery(getUserProfile)
    val postsQuery by useQuery(getUserPosts(userQuery.data?.id))

    // postsQuery only runs after userQuery succeeds
}
```

### Parallel Queries

```kotlin
@Composable
fun Dashboard() {
    val queries = useQueries(
        listOf(
            PostQueries.all,
            UserQueries.all,
            CommentQueries.recent,
            StatsQueries.overview
        )
    )

    val isLoading = queries.all { it.isLoading }
    val hasError = queries.any { it.isError }

    if (isLoading) {
        LoadingIndicator()
    } else if (hasError) {
        ErrorMessage("Failed to load dashboard data")
    } else {
        DashboardContent(
            posts = queries[0].data as List<Post>,
            users = queries[1].data as List<User>,
            comments = queries[2].data as List<Comment>,
            stats = queries[3].data as Stats
        )
    }
}
```

### Infinite Queries

```kotlin
val getInfinitePosts = infiniteQuery<PostsPage> {
    key = InfinitePostsKey()
    queryFn = { pageParam: Int ->
        httpClient.get("/api/posts") {
            parameter("page", pageParam)
            parameter("limit", 10)
        }.body<PostsPage>()
    }
    getNextPageParam = { lastPage ->
        if (lastPage.hasNext) lastPage.page + 1 else null
    }
    getPreviousPageParam = { firstPage ->
        if (firstPage.page > 1) firstPage.page - 1 else null
    }
    staleTime = 30.seconds
}

@Composable
fun InfinitePostsList() {
    val query = useInfiniteQuery(getInfinitePosts)

    LazyColumn {
        items(query.data?.pages?.flatMap { it.posts } ?: emptyList()) { post ->
            PostItem(post)
        }

        if (query.hasNextPage) {
            item {
                Button(
                    onClick = { query.fetchNextPage() },
                    enabled = !query.isFetchingNextPage
                ) {
                    Text(if (query.isFetchingNextPage) "Loading..." else "Load More")
                }
            }
        }
    }
}
```

### Prefetching

```kotlin
@Composable
fun PostsListWithPrefetch() {
    val queryClient = useQueryClient()
    val postsQuery by useQuery(PostQueries.all)

    LazyColumn {
        items(postsQuery.data ?: emptyList()) { post ->
            PostItem(
                post = post,
                onHover = {
                    // Prefetch post details on hover
                    queryClient.prefetchQuery(PostQueries.byId(post.id))
                }
            )
        }
    }
}
```

## Key Types

### Key Interface

```kotlin
interface Key {
    // Marker interface for type-safe keys
}

// Examples
data class PostsListKey(val filter: String = "all") : Key
data class PostDetailKey(val postId: Int) : Key
data class UserPostsKey(val userId: Int, val page: Int = 1) : Key
data object GlobalSettingsKey : Key
```

### Query Options

```kotlin
data class QueryOptions(
    val enabled: Boolean = true,
    val staleTime: Long = 0,
    val cacheTime: Long = 5 * 60 * 1000,
    val refetchInterval: Long? = null,
    val refetchOnWindowFocus: Boolean = true,
    val refetchOnReconnect: Boolean = true,
    val refetchOnMount: Boolean = true,
    val retry: RetryConfig = RetryConfig.Default,
    val suspense: Boolean = false,
    val keepPreviousData: Boolean = false,
    val structuralSharing: Boolean = true,
)
```

### Retry Configuration

```kotlin
data class RetryConfig(
    val maxAttempts: Int = 3,
    val delay: Long = 1000,
    val maxDelay: Long = 30000,
    val factor: Double = 2.0,
    val shouldRetry: (attempt: Int, error: Throwable) -> Boolean = { _, _ -> true }
) {
    companion object {
        val Default = RetryConfig()
        val None = RetryConfig(maxAttempts = 0)

        fun exponentialBackoff(
            maxAttempts: Int = 3,
            initialDelay: Long = 1000,
            maxDelay: Long = 30000,
            factor: Double = 2.0
        ) = RetryConfig(
            maxAttempts = maxAttempts,
            delay = initialDelay,
            maxDelay = maxDelay,
            factor = factor
        )

        fun linearBackoff(
            maxAttempts: Int = 3,
            delay: Long = 1000
        ) = RetryConfig(
            maxAttempts = maxAttempts,
            delay = delay,
            factor = 1.0
        )
    }
}
```

## Organization Pattern

### Recommended Project Structure

```
app/
├── src/
│   └── commonMain/
│       └── kotlin/
│           └── com/example/app/
│               ├── ui/
│               │   ├── screens/
│               │   │   ├── PostsListScreen.kt
│               │   │   ├── PostDetailScreen.kt
│               │   │   └── CreatePostScreen.kt
│               │   └── components/
│               │       ├── PostItem.kt
│               │       └── LoadingIndicator.kt
│               ├── queries/
│               │   ├── PostQueries.kt
│               │   ├── PostMutations.kt
│               │   ├── UserQueries.kt
│               │   └── UserMutations.kt
│               ├── models/
│               │   ├── Post.kt
│               │   └── User.kt
│               └── api/
│                   └── HttpClient.kt
```

### Query Organization Example

```kotlin
// queries/PostQueries.kt
object PostQueries {
    val all = query<List<Post>> {
        key = PostsListKey()
        queryFn = { api.getPosts() }
        staleTime = 5.minutes
    }

    fun byId(id: Int) = query<Post> {
        key = PostDetailKey(id)
        queryFn = { api.getPost(id) }
        staleTime = 30.seconds
    }

    fun byUser(userId: Int) = query<List<Post>> {
        key = UserPostsKey(userId)
        queryFn = { api.getUserPosts(userId) }
        staleTime = 2.minutes
    }
}

// queries/PostMutations.kt
object PostMutations {
    val create = mutation<Post, CreatePostRequest> {
        mutationFn = { request -> api.createPost(request) }
        onSuccess = { data, _, _ ->
            queryClient.invalidateQueries(PostsListKey())
        }
    }

    fun update(id: Int) = mutation<Post, UpdatePostRequest> {
        mutationFn = { request -> api.updatePost(id, request) }
        onSuccess = { data, _, _ ->
            queryClient.setQueryData(PostDetailKey(id), data)
            queryClient.invalidateQueries(PostsListKey())
        }
    }

    fun delete(id: Int) = mutation<Unit, Unit> {
        mutationFn = { api.deletePost(id) }
        onSuccess = { _, _, _ ->
            queryClient.removeQueries(PostDetailKey(id))
            queryClient.invalidateQueries(PostsListKey())
        }
    }
}
```

## Testing

### Testing Query Definitions

```kotlin
class PostQueriesTest {
    private val fakeHttpClient = FakeHttpClient()
    private val queryClient = QueryClient()

    @Test
    fun `getAllPosts query has correct configuration`() {
        val query = PostQueries.all

        assertEquals(PostsListKey(), query.key)
        assertEquals(5.minutes, query.options.staleTime)
        assertEquals(10.minutes, query.options.cacheTime)
        assertTrue(query.options.refetchOnWindowFocus)
    }

    @Test
    fun `create mutation handles optimistic updates`() = runTest {
        val mutation = PostMutations.create
        val request = CreatePostRequest("Title", "Body")

        // Set initial data
        queryClient.setQueryData(
            PostsListKey(),
            listOf(Post(1, "Existing", "Body"))
        )

        // Execute onMutate
        val context = mutation.onMutate?.invoke(request)

        // Verify optimistic update
        val posts = queryClient.getQueryData<List<Post>>(PostsListKey())
        assertEquals(2, posts?.size)

        // Simulate error and verify rollback
        mutation.onError?.invoke(
            Exception("Network error"),
            request,
            context
        )

        val rolledBack = queryClient.getQueryData<List<Post>>(PostsListKey())
        assertEquals(1, rolledBack?.size)
    }
}
```

### Testing Components with Queries

```kotlin
@Test
fun `PostsList displays loading state`() = runComposeTest {
    val fakeQuery = query<List<Post>> {
        key = PostsListKey()
        queryFn = {
            delay(1000)
            listOf(Post(1, "Test", "Body"))
        }
    }

    setContent {
        CompositionLocalProvider(
            LocalQueryClient provides testQueryClient
        ) {
            PostsList()
        }
    }

    // Verify loading state
    onNodeWithText("Loading...").assertExists()

    // Wait for data
    advanceTimeBy(1000)

    // Verify data is displayed
    onNodeWithText("Test").assertExists()
}
```

## Migration Guide

### From Inline Query Functions

```kotlin
// Before: Query function inline in UI
@Composable
fun PostsList() {
    val queryState by useQuery(
        key = PostsListKey(),
        queryFn = {
            httpClient.get("/api/posts").body<List<Post>>()
        }
    )
}

// After: Query definition separated
@Composable
fun PostsList() {
    val queryState by useQuery(PostQueries.all)
}
```

### From Repository Pattern

```kotlin
// Before: Repository pattern
class PostRepository {
    suspend fun getPosts(): List<Post> {
        return httpClient.get("/api/posts").body()
    }
}

@Composable
fun PostsList(repository: PostRepository) {
    val queryState by useQuery(
        key = PostsListKey(),
        queryFn = { repository.getPosts() }
    )
}

// After: Query definitions
object PostQueries {
    val all = query<List<Post>> {
        key = PostsListKey()
        queryFn = {
            httpClient.get("/api/posts").body()
        }
        staleTime = 5.minutes
    }
}

@Composable
fun PostsList() {
    val queryState by useQuery(PostQueries.all)
}
```

## Benefits

1. **Clean Separation** - UI components don't contain data fetching logic
2. **Reusability** - Query definitions can be used across multiple components
3. **Type Safety** - Strongly typed keys and return types
4. **Testability** - Easy to test queries in isolation
5. **Configuration** - All query options in one place
6. **Discoverability** - All queries for a domain grouped together
7. **Maintainability** - Single source of truth for data fetching

## Future Enhancements

1. **Query Composition** - Combine multiple queries into complex queries
2. **Query Middleware** - Add logging, metrics, or transformations
3. **Code Generation** - Generate query definitions from OpenAPI specs
4. **DevTools Integration** - Inspect queries in development
5. **Persistence** - Persist cache to disk for offline support