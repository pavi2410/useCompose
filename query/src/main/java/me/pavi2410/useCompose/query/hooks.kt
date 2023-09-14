package me.pavi2410.useCompose.query

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import kotlinx.coroutines.*

sealed interface QueryState<out T> {
    object Loading : QueryState<Nothing>
    data class Error(val message: Throwable) : QueryState<Nothing>
    data class Content<T>(val data: T) : QueryState<T>
}

sealed interface MutationState<T> {
    object Idle : MutationState<Nothing>
    object Loading : MutationState<Nothing>
    data class Error(val message: Throwable) : MutationState<Nothing>
    data class Content<T>(val data: T) : MutationState<T>
}

interface Mutation<T> {
    fun mutate(vararg args: String, callback: (T) -> Unit)
    fun cancel()
//    val state: MutationState<T>
}

@Composable
fun <T> useQuery(query: suspend CoroutineScope.() -> T): State<QueryState<T>> {
    return produceState<QueryState<T>>(initialValue = QueryState.Loading) {
        value = withContext(Dispatchers.IO) {
            val res = query()
            QueryState.Content(res)
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> useMutation(query: suspend CoroutineScope.(args: Array<out String>) -> T): Mutation<T> {
//    var mutationState by remember { mutableStateOf(MutationState.Idle) }
    val coroutineScope = rememberCoroutineScope()
    return object: Mutation<T> {
        override fun mutate(vararg args: String, callback: (T) -> Unit) {
            coroutineScope.launch {
//                mutationState = MutationState.Loading
                callback(query(args))
//                mutationState = MutationState.Success
            }
        }
        override fun cancel() {
            coroutineScope.cancel()
        }

//        override val state: MutationState<T>
//            get() = mutationState
    }
}