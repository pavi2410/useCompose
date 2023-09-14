package me.pavi2410.useCompose.demo.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ExampleScreenScaffold(navController: NavController, title: String, content: @Composable () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("Example / $title")
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            }
        )
    }) {
        content()
    }
}