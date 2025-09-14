package me.pavi2410.useCompose.query

import kotlin.test.*

class MutationTest {

    @Test
    fun mutationStateSealed_hasCorrectTypes() {
        // Test MutationState sealed interface structure
        val idle: MutationState<Nothing> = MutationState.Idle
        val loading: MutationState<Nothing> = MutationState.Loading
        val error: MutationState<Nothing> = MutationState.Error(RuntimeException("test"))
        val content: MutationState<String> = MutationState.Content("data")

        assertTrue(idle is MutationState.Idle)
        assertTrue(loading is MutationState.Loading)
        assertTrue(error is MutationState.Error)
        assertTrue(content is MutationState.Content)
        assertEquals("data", content.data)
    }

    @Test
    fun mutationStateError_preservesExceptionMessage() {
        val errorMessage = "Test mutation error"
        val exception = IllegalArgumentException(errorMessage)
        val errorState = MutationState.Error(exception)

        assertEquals(exception, errorState.message)
        assertEquals(errorMessage, errorState.message.message)
    }

    @Test
    fun mutationStateContent_preservesData() {
        val testResult = listOf("item1", "item2", "item3")
        val contentState = MutationState.Content(testResult)

        assertEquals(testResult, contentState.data)
        assertEquals(3, contentState.data.size)
        assertEquals("item1", contentState.data[0])
    }
}