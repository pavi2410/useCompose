package me.pavi2410.useCompose

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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