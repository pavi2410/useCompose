package com.pavi2410.useCompose.query

import kotlin.test.*

class QueryTest {

    @Test
    fun queryStateSealed_hasCorrectTypes() {
        // Test QueryState sealed interface structure
        val loading: QueryState<String> = QueryState.Loading
        val error: QueryState<String> = QueryState.Error(RuntimeException("test"))
        val content: QueryState<String> = QueryState.Content("data")

        assertTrue(loading is QueryState.Loading)
        assertTrue(error is QueryState.Error)
        assertTrue(content is QueryState.Content)
        assertEquals("data", content.data)
    }

    @Test
    fun queryStateError_preservesExceptionMessage() {
        val errorMessage = "Test error message"
        val exception = RuntimeException(errorMessage)
        val errorState = QueryState.Error(exception)

        assertEquals(exception, errorState.message)
        assertEquals(errorMessage, errorState.message.message)
    }

    @Test
    fun queryStateContent_preservesData() {
        val testData = mapOf("key" to "value", "count" to 42)
        val contentState = QueryState.Content(testData)

        assertEquals(testData, contentState.data)
        assertEquals("value", contentState.data["key"])
        assertEquals(42, contentState.data["count"])
    }
}