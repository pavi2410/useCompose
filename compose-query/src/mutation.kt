package com.pavi2410.useCompose.query

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface MutationState<out T> {
    object Idle : MutationState<Nothing>
    object Loading : MutationState<Nothing>
    data class Error(val message: String) : MutationState<Nothing>
    data class Success<T>(val data: T) : MutationState<T>
}

interface Mutation<T> {
    val mutationState: State<MutationState<T>>

    fun mutate(
        vararg args: String,
        onSuccess: (T) -> Unit = {},
        onError: (String) -> Unit = {},
    )

    fun cancel()
}

@Composable
fun <T> useMutation(mutationFn: suspend CoroutineScope.(args: Array<out String>) -> T): Mutation<T> {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        object : Mutation<T> {
            private val _mutationState = mutableStateOf<MutationState<T>>(MutationState.Idle)
            override val mutationState: State<MutationState<T>> = _mutationState

            override fun mutate(
                vararg args: String,
                onSuccess: (T) -> Unit,
                onError: (String) -> Unit,
            ) {
                _mutationState.value = MutationState.Loading
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val result = mutationFn(args)
                            _mutationState.value = MutationState.Success(result)
                            withContext(Dispatchers.Main) {
                                onSuccess(result)
                            }
                        } catch (e: Throwable) {
                            val errorMessage = e.message ?: "unknown error"
                            _mutationState.value = MutationState.Error(errorMessage)
                            withContext(Dispatchers.Main) {
                                onError(errorMessage)
                            }
                        }
                    }
                }
            }

            override fun cancel() {
                coroutineScope.cancel()
            }
        }
    }
}