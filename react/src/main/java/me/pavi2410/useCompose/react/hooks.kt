package me.pavi2410.useCompose.react

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope

/**
 * A React-ish hook that saves state across recompositions.
 *
 * @see [useState](https://reactjs.org/docs/hooks-state.html)
 */
@SuppressLint("ComposableNaming")
@Composable
fun <T> useState(defaultValue: T): Pair<T, (T) -> Unit> {
    val (state, setState) = remember { mutableStateOf(defaultValue) }

    return Pair(state, setState)
}

/**
 * A React-ish hook that triggers on recompositions or state changes.
 *
 * @see [useEffect](https://reactjs.org/docs/hooks-effect.html)
 */
@SuppressLint("ComposableNaming")
@Composable
fun useEffect(vararg deps: Any, block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(keys = deps, block = block)
}

@Suppress("PropertyName")
interface ReactContext<T> {
    val LocalCtx: ProvidableCompositionLocal<T>
    @Composable
    fun Provider(value: T, content: @Composable () -> Unit)
}

/**
 * Composable function to create an context object.
 */
@Composable
fun <T> createContext(initialValue: T): ReactContext<T> {
    return object: ReactContext<T> {
        override val LocalCtx = compositionLocalOf { initialValue }
        @Composable
        override fun Provider(value: T, content: @Composable () -> Unit) {
            CompositionLocalProvider(
                LocalCtx provides value,
                content = content
            )
        }
    }
}

/**
 * A React-ish hook that returns the current value for that context.
 *
 * @see [useEffect](https://reactjs.org/docs/hooks-effect.html)
 */
@SuppressLint("ComposableNaming")
@Composable
fun <T> useContext(context: ReactContext<T>): T {
    return context.LocalCtx.current
}