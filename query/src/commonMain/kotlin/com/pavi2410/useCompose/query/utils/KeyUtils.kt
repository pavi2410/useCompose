package com.pavi2410.useCompose.query.utils

import com.pavi2410.useCompose.query.core.Key
import com.pavi2410.useCompose.query.core.KeyMatcher

/**
 * Utility functions for working with query keys.
 */

/**
 * Create a matcher that matches all keys of the same type as the given key.
 */
fun Key.matchType(): KeyMatcher.ByType = KeyMatcher.ByType(this::class)

/**
 * Create a matcher that matches keys using a predicate on the same type.
 */
inline fun <reified T : Key> matchWhere(noinline predicate: (T) -> Boolean): KeyMatcher.Predicate =
    KeyMatcher.Predicate { key ->
        key is T && predicate(key)
    }

/**
 * Helper for matching all keys.
 */
fun matchAll(): KeyMatcher.Predicate = KeyMatcher.Predicate { true }

/**
 * Helper for matching no keys.
 */
fun matchNone(): KeyMatcher.Predicate = KeyMatcher.Predicate { false }