package com.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.pavi2410.useCompose.query.useMutation

@Composable
fun MutationExample(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Mutation Example", modifier = Modifier.padding(bottom = 16.dp))

        var token by remember { mutableStateOf("") }

        val loginMutation = useMutation { (username, password) ->
            delay(500)
            "secret_token:$username/$password"
        }

        Button(
            onClick = {
                loginMutation.mutate("pavi2410", "secretpw123") {
                    token = it
                }
            }
        ) {
            Text("Login")
        }

        Text(
            text = if (token.isEmpty()) "Please login" else "Welcome! token = $token",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}