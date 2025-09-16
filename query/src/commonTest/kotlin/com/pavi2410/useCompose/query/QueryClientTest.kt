package com.pavi2410.useCompose.query

import com.pavi2410.useCompose.query.core.QueryClient
import com.pavi2410.useCompose.query.core.QueryOptions
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

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

    @Test
    fun prefetchQuery_cachesDataWithoutReturning() = runTest {
        val client = QueryClient()
        val key = UserKey(123L)

        var callCount = 0

        // Prefetch should cache the data
        client.prefetchQuery(
            key,
            queryFn = {
                callCount++
                "user-123"
            }
        )
        assertEquals(1, callCount)

        // Subsequent getQuery should use cached data
        val entry = client.getQuery(key) {
            callCount++
            "user-456" // Different data, should not be called
        }
        assertEquals("user-123", entry.data)
        assertEquals(1, callCount) // Should not increase
    }

    @Test
    fun prefetchQuery_respectsStaleTime() = runTest {
        val client = QueryClient()
        val key = UserKey(123L)

        var callCount = 0

        // Initial prefetch with long stale time
        client.prefetchQuery(
            key = key,
            queryFn = {
                callCount++
                "user-$callCount"
            },
            options = QueryOptions(staleTime = 10000) // 10 seconds stale time
        )
        assertEquals(1, callCount)

        // Immediate prefetch should be skipped due to stale time
        client.prefetchQuery(
            key = key,
            queryFn = {
                callCount++
                "user-$callCount"
            },
            options = QueryOptions(staleTime = 10000)
        )
        assertEquals(1, callCount) // Should not increase

        // Test with 0 stale time (always stale)
        client.prefetchQuery(
            key = key,
            queryFn = {
                callCount++
                "user-$callCount"
            },
            options = QueryOptions(staleTime = 0) // Always stale
        )
        assertEquals(2, callCount) // Should increase
    }

    @Test
    fun prefetchQuery_handlesErrors() = runTest {
        val client = QueryClient()
        val key = UserKey(123L)

        var callCount = 0

        // Prefetch that throws error should not crash
        client.prefetchQuery(key, queryFn = {
            callCount++
            throw RuntimeException("Network error")
        })
        assertEquals(1, callCount)

        // Error should be cached, so getQuery should see it
        val entry = client.getQuery(key) {
            callCount++
            "user-success"
        }
        assertEquals("Network error", entry.error?.message)
        assertEquals(1, callCount) // Should not call queryFn again
    }

    @Test
    fun getQueryData_returnsNullWhenNoData() = runTest {
        val client = QueryClient()
        val key = UserKey(123L)

        // Should return null when no data is cached
        val data = client.getQueryData<String>(key)
        assertNull(data)
    }

    @Test
    fun getQueryData_returnsCachedData() = runTest {
        val client = QueryClient()
        val key = UserKey(123L)

        // Cache some data first
        client.getQuery(key) {
            "user-123"
        }

        // getQueryData should return the cached data
        val data = client.getQueryData<String>(key)
        assertEquals("user-123", data)
    }

    @Test
    fun getQueryData_returnsNullWhenDataHasError() = runTest {
        val client = QueryClient()
        val key = UserKey(123L)

        // Cache data with error - this should throw
        assertFails {
            client.getQuery(key) {
                throw RuntimeException("Error")
            }
        }

        // getQueryData should return null when there's an error
        val data = client.getQueryData<String>(key)
        assertNull(data)
    }

    @Test
    fun getQueryData_returnsNullWhenDataIsInvalidated() = runTest {
        val client = QueryClient()
        val key = UserKey(123L)

        // Cache some data first
        client.getQuery(key) {
            "user-123"
        }

        // Data should be available
        assertEquals("user-123", client.getQueryData<String>(key))

        // Invalidate the data
        client.invalidateQuery(key)

        // getQueryData should now return null
        val data = client.getQueryData<String>(key)
        assertNull(data)
    }
}