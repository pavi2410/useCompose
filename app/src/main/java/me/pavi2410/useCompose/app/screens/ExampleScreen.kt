package me.pavi2410.useCompose.app.screens

import androidx.compose.runtime.Composable

sealed class ExampleScreen(
    val route: String,
    val title: String,
    val content: @Composable () -> Unit
) {
    object Counter : ExampleScreen("counter", "Counter", { CounterExample() })
    object Context : ExampleScreen("context", "Context", { ContextExample() })
    object Reducer : ExampleScreen("reducer", "Reducer", { ReducerExample() })
    object Toggle : ExampleScreen("toggle", "Toggle", { ToggleExample() })
    object Network : ExampleScreen("network", "Network", { NetworkExample() })
    object Query : ExampleScreen("query", "Query", { QueryExample() })
    object Mutation : ExampleScreen("mutation", "Mutation", { MutationExample() })
}

val exampleScreens
    get() = listOf(
        ExampleScreen.Counter,
        ExampleScreen.Context,
        ExampleScreen.Reducer,
        ExampleScreen.Toggle,
        ExampleScreen.Network,
        ExampleScreen.Query,
        ExampleScreen.Mutation,
    )
