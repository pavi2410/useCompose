package com.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.pavi2410.useCompose.query.useQuery
import com.pavi2410.useCompose.query.QueryState

@Composable
fun QueryExample(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Query Example", modifier = Modifier.padding(bottom = 16.dp))

        val queryState by useQuery {
            delay(500)
            "secret_token_${System.currentTimeMillis()}"
        }

        when (val state = queryState) {
            is QueryState.Loading -> {
                Text("Loading data...")
            }
            is QueryState.Error -> {
                Text("Error: ${state.message.message}")
            }
            is QueryState.Content -> {
                Text("Success: ${state.data}")
            }
        }
    }
}