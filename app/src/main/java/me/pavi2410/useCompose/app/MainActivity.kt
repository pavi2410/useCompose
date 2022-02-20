package me.pavi2410.useCompose.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.pavi2410.useCompose.app.components.ExampleScreenScaffold
import me.pavi2410.useCompose.app.screens.MainScreen
import me.pavi2410.useCompose.app.screens.exampleScreens
import me.pavi2410.useCompose.app.theme.UseComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UseComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") { MainScreen(navController) }
                        exampleScreens.forEach { screen ->
                            composable(screen.route) {
                                ExampleScreenScaffold(navController, title = screen.title) {
                                    screen.content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}