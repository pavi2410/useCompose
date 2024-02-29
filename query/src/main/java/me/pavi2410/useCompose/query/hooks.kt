package me.pavi2410.useCompose.query

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import kotlinx.coroutines.*

sealed interface QueryState<out T> {
    object Loading : QueryState<Nothing>
    data class Error(val message: Throwable) : QueryState<Nothing>
    data class Content<T>(val data: T) : QueryState<T>
}

sealed interface MutationState<out T> {
    object Idle : MutationState<Nothing>
    object Loading : MutationState<Nothing>
    object Success : MutationState<Nothing>
    data class Error(val th: Throwable) : MutationState<Nothing>
//    data class Content<T>(val data: T) : MutationState<T> // we unknown callback return type
}

interface Mutation<T>  {
    fun mutate(vararg args: String, callback: (T) -> Unit)
    fun cancel()
    fun reset()
    val state: MutationState<T>
}

@Composable
fun <T> useQuery(query: suspend CoroutineScope.() -> T): State<QueryState<T>> {
    return produceState<QueryState<T>>(initialValue = QueryState.Loading) {
        value = withContext(Dispatchers.IO) {
            val res : T
            try {
                res = query()
                QueryState.Content(res)
            }catch (e : Exception){
                QueryState.Error(e)
            }
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> useMutation(query: suspend CoroutineScope.(args: Array<out String>) -> T): Mutation<T> {
    var mutationState : MutationState<Nothing> by remember { mutableStateOf(MutationState.Idle) }
    val coroutineScope = rememberCoroutineScope()
    return object: Mutation<T> {
        override fun mutate(vararg args: String, callback: (T) -> Unit) {
            coroutineScope.launch {
                mutationState = MutationState.Loading
                try {
                    callback(query(args))
                    mutationState = MutationState.Success
                    MutationState.Success
                } catch (e : Exception){
                    mutationState = MutationState.Error(e)
                    MutationState.Error(e)
                }

            }
        }
        override fun cancel() {
            coroutineScope.cancel()
        }

        override  fun reset () {
            mutationState = MutationState.Idle
        }

        override val state: MutationState<Nothing>
            get() = mutationState
    }
}
