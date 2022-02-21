package me.pavi2410.useCompose.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.delay
import me.pavi2410.useCompose.query.useMutation

@Composable
fun MutationExample() {
    Column {
        var token by remember { mutableStateOf("") }

        val loginMutation = useMutation { (username, password) ->
            delay(500)
            "secret_token:$username/$password"
        }

        Button(
            modifier = Modifier.testTag("login_button"),
            onClick = {
                // todo: is this blocking the main thread?
                // todo: this makes me think I need a mutateAsync too...
                loginMutation.mutate("pavi2410", "secretpw123") {
                    token = it
                }
            }
        ) {
            Text("Login")
        }

        Text(
            if (token.isEmpty()) "Please login" else "Welcome! token = $token",
            modifier = Modifier.testTag("status")
        )
    }
}