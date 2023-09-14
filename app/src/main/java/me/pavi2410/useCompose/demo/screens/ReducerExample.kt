package me.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.pavi2410.useCompose.react.useReducer

data class MyState(val count: Int)
sealed interface MyAction {
    object Increment : MyAction
    object Decrement : MyAction
}

val initialState = MyState(0)

@Preview(showBackground = true)
@Composable
fun ReducerExample() {
    Column(Modifier.padding(16.dp)) {
        val (state, dispatch) = useReducer<MyState, MyAction>(initialState) { state, action ->
            when (action) {
                MyAction.Increment -> state.copy(count = state.count + 1)
                MyAction.Decrement -> state.copy(count = state.count - 1)
            }
        }

        Text("Count: ${state.count}")
        Button(onClick = {
            dispatch(MyAction.Increment)
        }) {
            Text("+")
        }
        Button(onClick = {
            dispatch(MyAction.Decrement)
        }) {
            Text("-")
        }
    }
}