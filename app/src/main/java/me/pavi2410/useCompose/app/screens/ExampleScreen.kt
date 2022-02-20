package me.pavi2410.useCompose.app.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

sealed class ExampleScreen(
    val route: String,
    val title: String,
    val content: @Composable () -> Unit
) {
    object Counter : ExampleScreen("counter", "Counter", { CounterExample() })
    object Context : ExampleScreen("context", "Context", { ContextExample() })
    object Reducer : ExampleScreen("reducer", "Reducer", { ReducerExample() })
    object Network : ExampleScreen("network", "Network", { NetworkExample() })
}

val exampleScreens
    get() = listOf(
        ExampleScreen.Counter,
        ExampleScreen.Context,
        ExampleScreen.Reducer,
        ExampleScreen.Network
    )
