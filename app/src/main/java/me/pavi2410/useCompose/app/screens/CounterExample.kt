package me.pavi2410.useCompose.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import me.pavi2410.useCompose.app.components.ExampleScreenScaffold
import me.pavi2410.useCompose.react.useEffect
import me.pavi2410.useCompose.react.useState

@Composable
fun CounterExample(navController: NavController) {
    ExampleScreenScaffold(navController, "Counter") {
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
}