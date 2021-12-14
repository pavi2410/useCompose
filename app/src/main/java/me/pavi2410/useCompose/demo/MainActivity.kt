package me.pavi2410.useCompose.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.pavi2410.useCompose.demo.theme.UseComposeTheme
import me.pavi2410.useCompose.useEffect
import me.pavi2410.useCompose.useState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UseComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Counter()
                }
            }
        }
    }
}

@Composable
fun Counter() {
    val (count, setCount) = useState(0)

    useEffect {
        println("started")
    }

    useEffect(count) {
        println("Count is $count")
    }

    Column {
        Text("You clicked $count times")

        Button(onClick = { setCount(count + 1) }) {
            Text("Click me")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UseComposeTheme {
        Counter()
    }
}