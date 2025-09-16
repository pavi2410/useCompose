package com.pavi2410.useCompose.query.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Simple query client for managing queries and cache.
 */
class QueryClient {
    private val cache = QueryCache()

    /**
     * Get cached data for a key, or fetch it if not cached.
     */
    suspend fun <T> getQuery(
        key: Key,
        queryFn: suspend CoroutineScope.() -> T,
    ): CacheEntry<T> {
        // Try to get from cache first
        val cached = cache.get<T>(key)
        if (cached != null && !cached.isInvalidated) {
            return cached
        }

        // Not in cache or invalidated, fetch new data
        return try {
            val data = withContext(Dispatchers.IO) { queryFn() }
            val entry = CacheEntry(data)
            cache.set(key, entry)
            entry
        } catch (e: Throwable) {
            // If we have cached data, return it with error, otherwise rethrow
            if (cached != null) {
                val entry = CacheEntry(
                    data = cached.data,
                    error = e
                )
                cache.set(key, entry)
                entry
            } else {
                throw e
            }
        }
    }

    /**
     * Invalidate a specific query by key.
     */
    suspend fun invalidateQuery(key: Key) {
        cache.invalidate(key)
    }

    /**
     * Invalidate all queries of a specific type.
     */
    suspend fun invalidateQueries(matcher: KeyMatcher) {
        cache.invalidateMatching(matcher)
    }

    /**
     * Invalidate all queries of a specific type using reified generics.
     */
    suspend inline fun <reified T : Key> invalidateQueriesOfType() {
        invalidateQueries(KeyMatcher.ByType(T::class))
    }

    /**
     * Clear all cached queries.
     */
    suspend fun clear() {
        cache.clear()
    }

    /**
     * Prefetch a query without returning it.
     * Only fetches if the data is stale based on the staleTime option.
     */
    suspend fun <T> prefetchQuery(
        key: Key,
        queryFn: suspend CoroutineScope.() -> T,
        options: QueryOptions = QueryOptions.Default,
    ) {
        val cached = cache.get<T>(key)

        // Skip prefetch if data exists and is not stale
        if (cached != null && !cached.isInvalidated && !cached.isStale(options.staleTime)) {
            return
        }

        try {
            val data = withContext(Dispatchers.IO) { queryFn() }
            val entry = CacheEntry(data)
            cache.set(key, entry)
        } catch (e: Throwable) {
            // For prefetch, we don't propagate errors
            // Just cache the error if we don't have existing data
            if (cached == null) {
                val entry = CacheEntry(
                    data = null as T,
                    error = e
                )
                cache.set(key, entry)
            }
        }
    }

    /**
     * Get cached data for a key without fetching.
     * Returns null if no data is cached.
     */
    suspend fun <T> getQueryData(key: Key): T? {
        val cached = cache.get<T>(key)
        return if (cached != null && cached.error == null && !cached.isInvalidated) {
            cached.data
        } else {
            null
        }
    }
}