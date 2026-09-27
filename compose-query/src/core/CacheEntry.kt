package com.pavi2410.useCompose.query.core

import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Cached query result: either successful data or a failure.
 */
sealed interface CacheEntry<out T> {
    val timestamp: TimeMark
    val isInvalidated: Boolean

    fun isStale(staleTime: Duration): Boolean =
        staleTime == Duration.ZERO || timestamp.elapsedNow() > staleTime

    fun invalidate(): CacheEntry<T>

    data class Success<T>(
        val data: T,
        override val timestamp: TimeMark = TimeSource.Monotonic.markNow(),
        override val isInvalidated: Boolean = false,
    ) : CacheEntry<T> {
        override fun invalidate() = copy(isInvalidated = true)
    }

    data class Failure(
        val error: Throwable,
        override val timestamp: TimeMark = TimeSource.Monotonic.markNow(),
        override val isInvalidated: Boolean = false,
    ) : CacheEntry<Nothing> {
        override fun invalidate() = copy(isInvalidated = true)
    }
}
