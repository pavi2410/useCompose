package me.pavi2410.useCompose.reactQuery

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*

sealed interface QueryResult<out T> {
    object Loading : QueryResult<Nothing>
    data class Error(val message: Throwable) : QueryResult<Nothing>
    data class Content<T>(val data: T) : QueryResult<T>
}
//
//sealed interface MutationResult<T> {
//    abstract fun mutate(): T
//
//    data class
//}

interface Mutation<T> {
    fun mutate(vararg args: String, callback: (T) -> Unit)
    fun cancel()
}

@Composable
fun <T> useQuery(query: suspend CoroutineScope.() -> T): State<QueryResult<T>> {
    return produceState<QueryResult<T>>(initialValue = QueryResult.Loading) {
        value = withContext(Dispatchers.IO) {
            val res = query()
            QueryResult.Content(res)
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> useMutation(query: suspend CoroutineScope.(args: Array<out String>) -> T): Mutation<T> {
    val coroutineScope = rememberCoroutineScope()
    return object: Mutation<T> {
        override fun mutate(vararg args: String, callback: (T) -> Unit) {
            coroutineScope.launch {
                callback(query(args))
            }
        }
        override fun cancel() {
            coroutineScope.cancel()
        }
    }
}