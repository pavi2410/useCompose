package me.pavi2410.useCompose.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.pavi2410.useCompose.app.components.ExampleScreenScaffold
import me.pavi2410.useCompose.network.useConnectionStatus

@Composable
fun NetworkExample(navController: NavController) {
    ExampleScreenScaffold(navController, "Network") {
        Column {
            val isConnected = useConnectionStatus()

            if (isConnected) {
                Icon(Icons.Default.Check, null, tint = Color.Green, modifier = Modifier.size(64.dp))
            } else {
                Icon(Icons.Default.Clear, null, tint = Color.Red, modifier = Modifier.size(64.dp))
            }
        }
    }
}