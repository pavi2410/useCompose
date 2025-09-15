package com.pavi2410.useCompose.query

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.pavi2410.useCompose.query.core.QueryClient

/**
 * CompositionLocal for providing QueryClient throughout the Compose tree.
 */
val LocalQueryClient = compositionLocalOf<QueryClient> {
    error("QueryClient not provided! Wrap your app with QueryClientProvider.")
}

/**
 * Provides a QueryClient to the Compose tree.
 */
@Composable
fun QueryClientProvider(
    client: QueryClient,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalQueryClient provides client,
        content = content
    )
}

/**
 * Gets the current QueryClient from the Compose context.
 */
@Composable
fun useQueryClient(): QueryClient = LocalQueryClient.current