package com.pavi2410.useCompose.query

import com.pavi2410.useCompose.query.core.CacheEntry
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TestTimeSource

class CacheEntryTest {

    @Test
    fun cacheEntry_isNotStaleWhenFresh() {
        val entry = CacheEntry.Success("test-data")

        assertFalse(entry.isStale(1.seconds))
        assertFalse(entry.isStale(100.milliseconds))
        assertTrue(entry.isStale(Duration.ZERO))
    }

    @Test
    fun cacheEntry_isStaleAfterTime() {
        val timeSource = TestTimeSource()
        val entry = CacheEntry.Success("test-data", timestamp = timeSource.markNow())

        timeSource += 200.milliseconds

        assertTrue(entry.isStale(100.milliseconds))
        assertFalse(entry.isStale(1.seconds))
    }

    @Test
    fun cacheEntry_alwaysStaleWithZeroStaleTime() {
        val entry = CacheEntry.Success("test-data")

        assertTrue(entry.isStale(Duration.ZERO))

        val entry2 = CacheEntry.Success("test-data-2")
        assertTrue(entry2.isStale(Duration.ZERO))
    }

    @Test
    fun cacheEntry_invalidationPreservesTimestamp() {
        val entry = CacheEntry.Success("test-data")
        val originalTimestamp = entry.timestamp

        val invalidatedEntry = entry.invalidate()

        assertTrue(invalidatedEntry.timestamp == originalTimestamp)
        assertTrue(invalidatedEntry.isInvalidated)
    }

    @Test
    fun cacheEntry_stalenessCheckWorksWithInvalidation() {
        val timeSource = TestTimeSource()
        val entry = CacheEntry.Success("test-data", timestamp = timeSource.markNow())

        timeSource += 200.milliseconds
        val invalidatedEntry = entry.invalidate()

        assertTrue(invalidatedEntry.isStale(100.milliseconds))
        assertFalse(invalidatedEntry.isStale(1.seconds))
    }

    @Test
    fun cacheEntry_customTimestamp() {
        val timeSource = TestTimeSource()
        val entry = CacheEntry.Success("test-data", timestamp = timeSource.markNow())

        timeSource += 5.seconds

        assertTrue(entry.isStale(1.seconds))
        assertTrue(entry.isStale(4.seconds))
        assertFalse(entry.isStale(10.seconds))
    }
}
