package me.pavi2410.useCompose.app.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

sealed class Screen(val route: String, val content: @Composable (NavController) -> Unit) {
    object Main : Screen("main", { MainScreen(it) })
    object Counter : Screen("counter", { CounterExample(it) })
    object Context : Screen("context", { ContextExample(it) })
    object Network : Screen("network", { NetworkExample(it) })
}

val exampleScreens
    get() = listOf(
        Screen.Counter,
        Screen.Context,
        Screen.Network
    )

val allScreens get() = exampleScreens + Screen.Main
