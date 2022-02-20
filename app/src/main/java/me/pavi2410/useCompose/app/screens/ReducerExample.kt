package me.pavi2410.useCompose.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import me.pavi2410.useCompose.react.useReducer

data class MyState(val count: Int)
data class MyAction(val type: String)

val initialState = MyState(0)

@Composable
fun ReducerExample() {
    Column {
        val (state, dispatch) = useReducer<MyState, MyAction>(initialState) { state, action ->
            when (action.type) {
                "increment" -> state.copy(count = state.count + 1)
                "decrement" -> state.copy(count = state.count - 1)
                else -> throw Error()
            }
        }

        Text("Count: ${state.count}")
        Button(onClick = {
            dispatch(MyAction("increment"))
        }) {
            Text("+")
        }
        Button(onClick = {
            dispatch(MyAction("decrement"))
        }) {
            Text("-")
        }
    }
}