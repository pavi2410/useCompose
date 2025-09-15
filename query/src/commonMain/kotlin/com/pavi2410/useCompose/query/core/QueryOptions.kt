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
) {
    companion object {
        /**
         * Default query options.
         */
        val Default = QueryOptions()
    }
}