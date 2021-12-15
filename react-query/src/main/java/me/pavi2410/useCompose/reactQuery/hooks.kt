package me.pavi2410.useCompose.reactQuery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class QueryResult<out T> {
    object Loading : QueryResult<Nothing>()
    data class Error(val message: Throwable) : QueryResult<Nothing>()
    data class Content<T>(val data: T) : QueryResult<T>()
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