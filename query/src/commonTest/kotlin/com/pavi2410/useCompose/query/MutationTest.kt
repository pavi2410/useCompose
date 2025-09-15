package com.pavi2410.useCompose.query

import kotlin.test.*

class MutationTest {

    @Test
    fun mutationStateSealed_hasCorrectTypes() {
        // Test MutationState sealed interface structure
        val idle: MutationState<Nothing> = MutationState.Idle
        val loading: MutationState<Nothing> = MutationState.Loading
        val error: MutationState<Nothing> = MutationState.Error("test")
        val success: MutationState<String> = MutationState.Success("data")

        assertTrue(idle is MutationState.Idle)
        assertTrue(loading is MutationState.Loading)
        assertTrue(error is MutationState.Error)
        assertTrue(success is MutationState.Success)
        assertEquals("data", success.data)
    }

    @Test
    fun mutationStateError_preservesExceptionMessage() {
        val errorMessage = "Test mutation error"
        val errorState = MutationState.Error(errorMessage)

        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun mutationStateContent_preservesData() {
        val testResult = listOf("item1", "item2", "item3")
        val successState = MutationState.Success(testResult)

        assertEquals(testResult, successState.data)
        assertEquals(3, successState.data.size)
        assertEquals("item1", successState.data[0])
    }
}