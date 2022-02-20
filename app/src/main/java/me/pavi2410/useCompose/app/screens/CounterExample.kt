package me.pavi2410.useCompose.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import me.pavi2410.useCompose.react.useEffect
import me.pavi2410.useCompose.react.useState

@Composable
fun CounterExample() {
    Column {
        val (count, setCount) = useState(0)

        useEffect {
            println("started")
        }

        useEffect(count) {
            println("Count is $count")
        }

        Text("You clicked $count times")

        Button(onClick = { setCount(count + 1) }) {
            Text("Click me")
        }
    }
}