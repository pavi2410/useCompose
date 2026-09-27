package com.pavi2410.useCompose.query.core

import kotlin.time.Duration

/**
 * Simple configuration options for queries.
 */
data class QueryOptions(
    /**
     * Whether the query is enabled.
     * Disabled queries will not execute automatically.
     * Default: true
     */
    val enabled: Boolean = true,
    /**
     * How long data remains fresh.
     * If data is younger than staleTime, prefetch will be skipped.
     * Default: [Duration.ZERO] (always stale)
     */
    val staleTime: Duration = Duration.ZERO,
) {
    companion object {
        /**
         * Default query options.
         */
        val Default = QueryOptions()
    }
}
