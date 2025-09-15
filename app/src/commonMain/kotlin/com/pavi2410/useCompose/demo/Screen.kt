package com.pavi2410.useCompose.demo

sealed interface Screen {
    val title: String

    data object Home : Screen {
        override val title = "useCompose Demo"
    }

    data object Query : Screen {
        override val title = "Query Example"
    }

    data object Mutation : Screen {
        override val title = "Mutation Example"
    }

    data object Simple : Screen {
        override val title = "Simple Example"
    }

    data object Basic : Screen {
        override val title = "Basic Example"
    }
}