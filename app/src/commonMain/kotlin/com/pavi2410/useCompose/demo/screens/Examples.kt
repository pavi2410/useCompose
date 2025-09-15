package com.pavi2410.useCompose.demo.screens

import com.pavi2410.useCompose.demo.Screen

data class ExampleScreen(
    val screen: Screen,
    val title: String
)

val exampleScreens = listOf(
    ExampleScreen(Screen.Query, "Query"),
    ExampleScreen(Screen.Mutation, "Mutation")
)