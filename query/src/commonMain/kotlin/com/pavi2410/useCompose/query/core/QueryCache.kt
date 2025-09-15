package com.pavi2410.useCompose.query.core

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Simple cache entry containing query data and metadata.
 */
data class CacheEntry<T>(
    val data: T,
    val error: Throwable? = null,
    val isInvalidated: Boolean = false,
) {
    /**
     * Create a copy marked as invalidated.
     */
    fun invalidate(): CacheEntry<T> = copy(isInvalidated = true)
}

/**
 * Simple thread-safe query cache.
 */
class QueryCache {
    private val cache = mutableMapOf<Key, CacheEntry<*>>()
    private val mutex = Mutex()

    /**
     * Get a cache entry for the given key.
     */
    suspend fun <T> get(key: Key): CacheEntry<T>? = mutex.withLock {
        @Suppress("UNCHECKED_CAST")
        cache[key] as? CacheEntry<T>
    }

    /**
     * Set a cache entry for the given key.
     */
    suspend fun <T> set(key: Key, entry: CacheEntry<T>) = mutex.withLock {
        cache[key] = entry
    }

    /**
     * Invalidate a specific cache entry.
     */
    suspend fun invalidate(key: Key) = mutex.withLock {
        cache[key]?.let { entry ->
            @Suppress("UNCHECKED_CAST")
            cache[key] = (entry as CacheEntry<Any>).invalidate()
        }
    }

    /**
     * Invalidate all cache entries matching the given matcher.
     */
    suspend fun invalidateMatching(matcher: KeyMatcher): Int = mutex.withLock {
        var count = 0
        cache.keys.filter { it.matches(matcher) }.forEach { key ->
            cache[key]?.let { entry ->
                @Suppress("UNCHECKED_CAST")
                cache[key] = (entry as CacheEntry<Any>).invalidate()
                count++
            }
        }
        count
    }

    /**
     * Clear all cache entries.
     */
    suspend fun clear() = mutex.withLock {
        cache.clear()
    }
}