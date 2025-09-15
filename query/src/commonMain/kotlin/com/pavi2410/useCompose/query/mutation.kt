package com.pavi2410.useCompose.query

import androidx.compose.runtime.*
import kotlinx.coroutines.*

sealed interface MutationState<T> {
    object Idle : MutationState<Nothing>
    object Loading : MutationState<Nothing>
    data class Error(val message: String) : MutationState<Nothing>
    data class Success<T>(val data: T) : MutationState<T>
}

interface Mutation<T> {
    fun mutate(vararg args: String, callback: (T) -> Unit)
    fun cancel()
}

@Composable
fun <T> useMutation(query: suspend CoroutineScope.(args: Array<out String>) -> T): Mutation<T> {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        object : Mutation<T> {
            override fun mutate(vararg args: String, callback: (T) -> Unit) {
                coroutineScope.launch {
                    try {
                        val result = query(args)
                        callback(result)
                    } catch (e: Throwable) {
                        // Handle error - could be extended to support error callbacks
                        println("Mutation error: ${e.message}")
                    }
                }
            }

            override fun cancel() {
                coroutineScope.cancel()
            }
        }
    }
}