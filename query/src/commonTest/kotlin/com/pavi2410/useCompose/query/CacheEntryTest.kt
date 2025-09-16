package com.pavi2410.useCompose.query

import com.pavi2410.useCompose.query.core.CacheEntry
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CacheEntryTest {

    @Test
    fun cacheEntry_hasCurrentTimestamp() {
        val beforeTime = System.currentTimeMillis()
        val entry = CacheEntry("test-data")
        val afterTime = System.currentTimeMillis()

        // Timestamp should be between before and after time
        assertTrue(entry.timestamp >= beforeTime)
        assertTrue(entry.timestamp <= afterTime)
    }

    @Test
    fun cacheEntry_isNotStaleWhenFresh() {
        val entry = CacheEntry("test-data")

        // Data should not be stale immediately with non-zero stale time
        assertFalse(entry.isStale(1000)) // 1 second stale time
        assertFalse(entry.isStale(100))  // 100ms stale time
        assertTrue(entry.isStale(0))     // 0ms stale time (always stale)
    }

    @Test
    fun cacheEntry_isStaleAfterTime() {
        // Create entry with timestamp from past
        val pastTime = System.currentTimeMillis() - 200 // 200ms ago
        val entry = CacheEntry("test-data", timestamp = pastTime)

        // Should be stale with short stale time
        assertTrue(entry.isStale(100)) // 100ms stale time

        // Should not be stale with longer stale time
        assertFalse(entry.isStale(1000)) // 1 second stale time
    }

    @Test
    fun cacheEntry_alwaysStaleWithZeroStaleTime() {
        val entry = CacheEntry("test-data")

        // Even immediately, should be stale with 0 stale time
        assertTrue(entry.isStale(0))

        // Create another entry and check immediately
        val entry2 = CacheEntry("test-data-2")
        assertTrue(entry2.isStale(0))
    }

    @Test
    fun cacheEntry_invalidationPreservesTimestamp() {
        val entry = CacheEntry("test-data")
        val originalTimestamp = entry.timestamp

        val invalidatedEntry = entry.invalidate()

        // Timestamp should be preserved
        assertTrue(invalidatedEntry.timestamp == originalTimestamp)
        assertTrue(invalidatedEntry.isInvalidated)
    }

    @Test
    fun cacheEntry_stalenessCheckWorksWithInvalidation() {
        // Create entry with timestamp from past
        val pastTime = System.currentTimeMillis() - 200 // 200ms ago
        val entry = CacheEntry("test-data", timestamp = pastTime)

        val invalidatedEntry = entry.invalidate()

        // Staleness check should still work on invalidated entries
        assertTrue(invalidatedEntry.isStale(100)) // 100ms stale time
        assertFalse(invalidatedEntry.isStale(1000)) // 1 second stale time
    }

    @Test
    fun cacheEntry_customTimestamp() {
        val customTime = System.currentTimeMillis() - 5000 // 5 seconds ago
        val entry = CacheEntry("test-data", timestamp = customTime)

        // Should be stale with short stale time
        assertTrue(entry.isStale(1000)) // 1 second stale time

        // Should be stale with longer stale time too
        assertTrue(entry.isStale(4000)) // 4 seconds stale time

        // Should not be stale with very long stale time
        assertFalse(entry.isStale(10000)) // 10 seconds stale time
    }
}