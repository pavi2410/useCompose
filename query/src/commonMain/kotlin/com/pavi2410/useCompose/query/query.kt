package com.pavi2410.useCompose.query

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.pavi2410.useCompose.query.core.Key
import com.pavi2410.useCompose.query.core.QueryOptions
import kotlinx.coroutines.CoroutineScope

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

/**
 * Use a query with type-safe key and QueryClient integration.
 */
@Composable
fun <T> useQuery(
    key: Key,
    queryFn: suspend CoroutineScope.() -> T,
    options: QueryOptions = QueryOptions.Default,
): State<QueryState<T>> {
    val queryClient = useQueryClient()

    return produceState<QueryState<T>>(
        initialValue = QueryState(FetchStatus.Idle, DataState.Pending),
        key1 = key,
        key2 = options.enabled
    ) {
        if (!options.enabled) {
            value = QueryState(FetchStatus.Idle, DataState.Pending)
            return@produceState
        }

        value = QueryState(FetchStatus.Fetching, value.dataState)

        value = try {
            val cacheEntry = queryClient.getQuery(key, queryFn)

            if (cacheEntry.error != null) {
                QueryState(
                    FetchStatus.Idle,
                    DataState.Error(cacheEntry.error.message ?: "unknown error")
                )
            } else {
                QueryState(FetchStatus.Idle, DataState.Success(cacheEntry.data))
            }
        } catch (e: Throwable) {
            QueryState(FetchStatus.Idle, DataState.Error(e.message ?: "unknown error"))
        }
    }
}
