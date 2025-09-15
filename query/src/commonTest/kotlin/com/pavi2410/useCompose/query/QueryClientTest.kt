package com.pavi2410.useCompose.query

import com.pavi2410.useCompose.query.core.QueryClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class QueryClientTest {

    @Test
    fun queryClient_cachesAndReturnsData() = runTest {
        val client = QueryClient()
        val key = UserKey(123L)

        var callCount = 0

        // First call should execute the query
        val entry1 = client.getQuery(key) {
            callCount++
            "user-123"
        }
        assertEquals("user-123", entry1.data)
        assertEquals(1, callCount)

        // Second call should return cached data
        val entry2 = client.getQuery(key) {
            callCount++
            "user-123"
        }
        assertEquals("user-123", entry2.data)
        assertEquals(1, callCount) // Should not increase
    }

    @Test
    fun queryClient_invalidationForcesRefetch() = runTest {
        val client = QueryClient()
        val key = UserKey(123L)

        var callCount = 0

        // First call
        val entry1 = client.getQuery(key) {
            callCount++
            "user-$callCount"
        }
        assertEquals("user-1", entry1.data)
        assertEquals(1, callCount)

        // Invalidate and call again
        client.invalidateQuery(key)
        val entry2 = client.getQuery(key) {
            callCount++
            "user-$callCount"
        }
        assertEquals("user-2", entry2.data)
        assertEquals(2, callCount)
    }

    @Test
    fun queryClient_invalidatesByType() = runTest {
        val client = QueryClient()
        val userKey1 = UserKey(123L)
        val userKey2 = UserKey(456L)
        val postKey = PostKey("abc")

        var userCallCount = 0
        var postCallCount = 0

        // Cache some data
        client.getQuery(userKey1) {
            userCallCount++
            "user-$userCallCount"
        }
        client.getQuery(userKey2) {
            userCallCount++
            "user-$userCallCount"
        }
        client.getQuery(postKey) {
            postCallCount++
            "post-$postCallCount"
        }

        assertEquals(2, userCallCount)
        assertEquals(1, postCallCount)

        // Invalidate all user queries
        client.invalidateQueriesOfType<UserKey>()

        // UserKey queries should refetch, PostKey should not
        client.getQuery(userKey1) {
            userCallCount++
            "user-$userCallCount"
        }
        client.getQuery(postKey) {
            postCallCount++
            "post-$postCallCount"
        }

        assertEquals(3, userCallCount) // UserKey refetched
        assertEquals(1, postCallCount) // PostKey used cache
    }
}