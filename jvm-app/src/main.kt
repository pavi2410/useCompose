import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.pavi2410.useCompose.demo.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "useCompose Demo"
    ) {
        App()
    }
}