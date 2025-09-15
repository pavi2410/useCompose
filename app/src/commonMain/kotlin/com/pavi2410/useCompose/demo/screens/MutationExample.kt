package com.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.pavi2410.useCompose.query.useMutation

@Composable
fun MutationExample(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        var username by remember { mutableStateOf("useCompose") }
        var password by remember { mutableStateOf("plsUseCompose!") }
        var token by remember { mutableStateOf("") }

        val loginMutation = useMutation { (username, password) ->
            delay(3000)
            if (username != "useCompose" || password != "plsUseCompose!") {
                throw Exception("Invalid credentials")
            }
            "secret_token:$username/$password"
        }

        TextField(
            username,
            { username = it },
            label = { Text("Username") }
        )

        TextField(
            password,
            { password = it },
            label = { Text("Password") }
        )

        Button(
            onClick = {
                loginMutation.mutate(username, password) {
                    token = it
                }
            }
        ) {
            Text("Login")
        }

        Text(
            text = if (token.isEmpty()) "Please login..." else "Welcome! token = $token",
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(text = "Mutation = $loginMutation")
    }
}