package com.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavi2410.useCompose.query.DataState
import com.pavi2410.useCompose.query.FetchStatus
import com.pavi2410.useCompose.query.core.Key
import com.pavi2410.useCompose.query.useQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RepoData(
    val full_name: String,
    val description: String,
    val subscribers_count: Int,
    val stargazers_count: Int,
    val forks_count: Int,
)

data class RepoKey(val repoPath: String) : Key

val client = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

@Composable
fun SimpleExample(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {

        val queryState by useQuery(
            key = RepoKey("pavi2410/useCompose"),
            queryFn = {
                client.get("https://api.github.com/repos/pavi2410/useCompose").body<RepoData>()
            }
        )

        when (val state = queryState.dataState) {
            is DataState.Pending -> {
                Text("Loading...")
            }

            is DataState.Error -> {
                Text("Error: ${state.message}")
            }

            is DataState.Success<*> -> {
                val data = state.data as RepoData
                Column {
                    Text("Name: ${data.full_name}")
                    Text("Description: ${data.description}")
                    Text("Subscribers: ${data.subscribers_count}")
                    Text("Stargazers: ${data.stargazers_count}")
                    Text("Forks: ${data.forks_count}")
                    if (queryState.fetchStatus == FetchStatus.Fetching) {
                        Text("Updating...")
                    }
                }
            }
        }
    }
}