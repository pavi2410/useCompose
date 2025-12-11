package com.pavi2410.useCompose.query

import com.pavi2410.useCompose.query.core.Key
import com.pavi2410.useCompose.query.core.KeyMatcher
import com.pavi2410.useCompose.query.core.keyMatching
import com.pavi2410.useCompose.query.core.keyTypeOf
import com.pavi2410.useCompose.query.core.matches
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

// Test keys
data class UserKey(val userId: Long) : Key
data class PostKey(val postId: String) : Key

class KeyTest {

    @Test
    fun keyEquality_worksCorrectly() {
        val key1 = UserKey(123L)
        val key2 = UserKey(123L)
        val key3 = UserKey(456L)

        assertEquals(key1, key2)
        assertNotEquals(key1, key3)
    }

    @Test
    fun keyMatching_exactMatch() {
        val key = UserKey(123L)
        val matcher = KeyMatcher.Exact(key)

        assertTrue(key.matches(matcher))
        assertFalse(UserKey(456L).matches(matcher))
    }

    @Test
    fun keyMatching_typeMatch() {
        val userKey = UserKey(123L)
        val postKey = PostKey("abc")
        val matcher = keyTypeOf<UserKey>()

        assertTrue(userKey.matches(matcher))
        assertFalse(postKey.matches(matcher))
    }

    @Test
    fun keyMatching_predicateMatch() {
        val key1 = UserKey(123L)
        val key2 = UserKey(456L)
        val matcher = keyMatching { key ->
            key is UserKey && key.userId > 200L
        }

        assertFalse(key1.matches(matcher))
        assertTrue(key2.matches(matcher))
    }
}