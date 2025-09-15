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
}