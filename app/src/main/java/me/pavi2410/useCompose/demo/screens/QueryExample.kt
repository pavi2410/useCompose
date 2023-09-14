package me.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.delay
import me.pavi2410.useCompose.query.useQuery

@Composable
fun QueryExample() {
    Column {
        val data = useQuery {
            delay(500)
            "secret_token"
        }

        Text(data.toString())
    }
}