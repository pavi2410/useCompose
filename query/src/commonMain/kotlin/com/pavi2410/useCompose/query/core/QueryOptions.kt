package com.pavi2410.useCompose.query.core

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
     * Time in milliseconds for how long data remains fresh.
     * If data is younger than staleTime, prefetch will be skipped.
     * Default: 0 (always stale)
     */
    val staleTime: Long = 0,
) {
    companion object {
        /**
         * Default query options.
         */
        val Default = QueryOptions()
    }
}