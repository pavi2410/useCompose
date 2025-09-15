package com.pavi2410.useCompose.query

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class QueryState<T>(
    val fetchStatus: FetchStatus,
    val dataState: DataState<T>,
)

enum class FetchStatus {
    Idle, Fetching
}

sealed interface DataState<out T> {
    object Pending : DataState<Nothing>
    data class Error(val message: String) : DataState<Nothing>
    data class Success<T>(val data: T) : DataState<T>
}

@Composable
fun <T> useQuery(query: suspend CoroutineScope.() -> T): State<QueryState<T>> {
    return produceState<QueryState<T>>(initialValue = QueryState(FetchStatus.Idle, DataState.Pending)) {
        try {
            val res = withContext(Dispatchers.IO) {
                query()
            }
            value = QueryState(FetchStatus.Idle, DataState.Success(res))
        } catch (e: Throwable) {
            value = QueryState(FetchStatus.Idle, DataState.Error(e.message ?: "unknown error"))
        }
    }
}