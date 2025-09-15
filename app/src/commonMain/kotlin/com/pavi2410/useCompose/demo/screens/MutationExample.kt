package com.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavi2410.useCompose.query.MutationState
import com.pavi2410.useCompose.query.useMutation
import kotlinx.coroutines.delay

@Composable
fun MutationExample(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        var username by remember { mutableStateOf("useCompose") }
        var password by remember { mutableStateOf("plsUseCompose!") }
        var token by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        val loginMutation = useMutation { (username, password) ->
            delay(3000)
            if (username != "useCompose" || password != "plsUseCompose!") {
                throw Exception("Invalid credentials")
            }
            "secret_token:$username/$password@${System.currentTimeMillis()}"
        }
        val mutationState by loginMutation.mutationState

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
            enabled = mutationState != MutationState.Loading,
            onClick = {
                loginMutation.mutate(
                    username, password,
                    onSuccess = { token = it },
                    onError = { errorMessage = it }
                )
            }
        ) {
            Text(
                text = when (mutationState) {
                    MutationState.Idle -> "Login"
                    MutationState.Loading -> "Logging in..."
                    is MutationState.Error -> "Retry"
                    is MutationState.Success -> "Re login"
                }
            )
        }

        when (mutationState) {
            MutationState.Idle -> {
                Text(
                    text = "Please login!",
                )
            }

            MutationState.Loading -> {
                Text(
                    text = "Please wait...",
                )
            }
            is MutationState.Error -> {
                Text(
                    text = "Error: $errorMessage",
                )
            }

            is MutationState.Success -> {
                val token = (mutationState as MutationState.Success<String>).data
                Text(
                    text = "Welcome! token = $token",
                )
            }
        }

        Text(
            text = "Token from onSuccess = $token"
        )
        Text(
            text = "Error from onError = $errorMessage"
        )
    }
}