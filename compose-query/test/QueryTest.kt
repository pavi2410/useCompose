package com.pavi2410.useCompose.query

import kotlin.test.*

class QueryTest {

    @Test
    fun queryStateSealed_hasCorrectTypes() {
        // Test QueryState sealed interface structure
        val loading: DataState<String> = DataState.Pending
        val error: DataState<String> = DataState.Error("test")
        val content: DataState<String> = DataState.Success("data")

        assertTrue(loading is DataState.Pending)
        assertTrue(error is DataState.Error)
        assertTrue(content is DataState.Success)
        assertEquals("data", content.data)
    }

    @Test
    fun queryStateError_preservesExceptionMessage() {
        val errorMessage = "Test error message"
        val errorState = DataState.Error(errorMessage)

        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun queryStateContent_preservesData() {
        val testData = mapOf("key" to "value", "count" to 42)
        val contentState = DataState.Success(testData)

        assertEquals(testData, contentState.data)
        assertEquals("value", contentState.data["key"])
        assertEquals(42, contentState.data["count"])
    }
}