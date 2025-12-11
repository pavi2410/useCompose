package com.pavi2410.useCompose.query.core

/**
 * Type-safe query key interface.
 *
 * Implement this interface on data classes to create strongly-typed query keys.
 * The data class automatically provides equality and hashing for cache operations.
 *
 * Example:
 * ```kotlin
 * data class UserKey(val userId: Long) : Key
 * data class PostsKey(val userId: Long, val page: Int = 1) : Key
 * ```
 */
interface Key

/**
 * Extended key interface for hierarchical relationships.
 * Allows for cascade invalidation patterns.
 */
interface HierarchicalKey : Key {
    /**
     * Returns the parent key in the hierarchy.
     * Used for cascade invalidation.
     */
    fun parentKey(): Key?
}

/**
 * Key matcher for flexible key matching and invalidation.
 */
sealed interface KeyMatcher {
    /**
     * Match a specific key exactly.
     */
    data class Exact(val key: Key) : KeyMatcher

    /**
     * Match all keys of a specific type.
     */
    data class ByType(val type: kotlin.reflect.KClass<out Key>) : KeyMatcher

    /**
     * Match keys using a custom predicate.
     */
    data class Predicate(val predicate: (Key) -> Boolean) : KeyMatcher

    /**
     * Match keys that start with a prefix (for hierarchical keys).
     */
    data class Prefix(val prefix: Key) : KeyMatcher
}

/**
 * Matches a key against a matcher.
 */
fun Key.matches(matcher: KeyMatcher): Boolean {
    return when (matcher) {
        is KeyMatcher.Exact -> this == matcher.key
        is KeyMatcher.ByType -> matcher.type.isInstance(this)
        is KeyMatcher.Predicate -> matcher.predicate(this)
        is KeyMatcher.Prefix -> {
            // For prefix matching, we check if this key "starts with" the prefix
            // This is useful for hierarchical keys where we want to match all related keys
            this.toString().startsWith(matcher.prefix.toString())
        }
    }
}

/**
 * Creates a type-based matcher.
 */
inline fun <reified T : Key> keyTypeOf(): KeyMatcher.ByType =
    KeyMatcher.ByType(T::class)

/**
 * Creates an exact key matcher.
 */
fun Key.toMatcher(): KeyMatcher.Exact = KeyMatcher.Exact(this)

/**
 * Creates a predicate-based matcher.
 */
fun keyMatching(predicate: (Key) -> Boolean): KeyMatcher.Predicate =
    KeyMatcher.Predicate(predicate)