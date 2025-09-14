package me.pavi2410.useCompose.demo.screens

import androidx.compose.runtime.Composable

sealed class ExampleScreen(
    val route: String,
    val title: String,
    val content: @Composable () -> Unit
) {
    object Query : ExampleScreen("query", "Query", { QueryExample() })
    object Mutation : ExampleScreen("mutation", "Mutation", { MutationExample() })
}

val exampleScreens
    get() = listOf(
        ExampleScreen.Query,
        ExampleScreen.Mutation,
    )
