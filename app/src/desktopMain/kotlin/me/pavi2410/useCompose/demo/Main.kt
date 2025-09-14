package me.pavi2410.useCompose.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "useCompose Demo"
    ) {
        App()
    }
}