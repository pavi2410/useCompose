package me.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import me.pavi2410.useCompose.hooks.useToggle

@Composable
fun ToggleExample() {
    Column {
        val (state, toggle) = useToggle()

        Text("Switch = ${if (state) "ON" else "OFF"}")

        Button(onClick = { toggle() }) {
            Text("Toggle")
        }
    }
}
