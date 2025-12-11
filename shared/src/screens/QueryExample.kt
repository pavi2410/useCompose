package com.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavi2410.useCompose.query.DataState
import com.pavi2410.useCompose.query.core.Key
import com.pavi2410.useCompose.query.useQuery
import kotlinx.coroutines.delay

data object TokenKey : Key

@Composable
fun QueryExample(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        val startTime = remember { System.currentTimeMillis() }

        val queryState by useQuery(
            key = TokenKey,
            queryFn = {
                delay(3000)
                "secret_token@${System.currentTimeMillis()}"
            }
        )

        Text("Started at $startTime")

        when (val state = queryState.dataState) {
            is DataState.Pending -> {
                Text("Loading data...")
            }

            is DataState.Error -> {
                Text("Error: ${state.message}")
            }

            is DataState.Success<*> -> {
                Text("Success: ${state.data}")
            }
        }
    }
}