package me.pavi2410.useCompose.query

import androidx.compose.runtime.*
import kotlinx.coroutines.*

sealed interface QueryState<out T> {
    data object Loading : QueryState<Nothing>
    data class Error(val message: Throwable) : QueryState<Nothing>
    data class Content<T>(val data: T) : QueryState<T>
}

@Composable
fun <T> useQuery(query: suspend CoroutineScope.() -> T): State<QueryState<T>> {
    return produceState<QueryState<T>>(initialValue = QueryState.Loading) {
        try {
            val res = withContext(Dispatchers.Default) {
                query()
            }
            value = QueryState.Content(res)
        } catch (e: Throwable) {
            value = QueryState.Error(e)
        }
    }
}