package com.pavi2410.useCompose.demo

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pavi2410.useCompose.demo.screens.MainScreen
import com.pavi2410.useCompose.demo.screens.MutationExample
import com.pavi2410.useCompose.demo.screens.QueryExample
import com.pavi2410.useCompose.demo.theme.UseComposeTheme

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
}

@Composable
fun App() {
    UseComposeTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        val showBackButton = currentScreen != Screen.Home

        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    navigationIcon = if (showBackButton) {
                        {
                            IconButton(onClick = { currentScreen = Screen.Home }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    } else null
                )
            }
        ) { paddingValues ->
            when (currentScreen) {
                Screen.Home -> MainScreen(
                    onNavigate = { screen -> currentScreen = screen },
                    modifier = Modifier.padding(paddingValues)
                )
                Screen.Query -> QueryExample(
                    modifier = Modifier.padding(paddingValues)
                )
                Screen.Mutation -> MutationExample(
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}