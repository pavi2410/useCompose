package me.pavi2410.useCompose.hooks

import androidx.compose.runtime.*

/**
 * See [https://usehooks.com/useToggle/]
 */
@Composable
fun useToggle(initialState: Boolean = false): Pair<Boolean, () -> Unit> {
    var state by remember { mutableStateOf(initialState) }
    val toggle = remember { { state = !state } }
    return Pair(state, toggle)
}
