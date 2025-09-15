package com.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.pavi2410.useCompose.query.DataState
import com.pavi2410.useCompose.query.FetchStatus
import com.pavi2410.useCompose.query.useQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Post(
    val id: Int,
    val title: String,
    val body: String,
)

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

@Composable
fun BasicExample(modifier: Modifier = Modifier) {
    var postId by remember { mutableIntStateOf(-1) }
    val visitedPosts = remember { mutableSetOf<Int>() }

    Column(modifier = modifier.padding(16.dp)) {
        // Description text
        Text(
            text = "As you visit the posts below, you will notice them in a loading state " +
                    "the first time you load them. However, after you return to this list and " +
                    "click on any posts you have already visited again, you will see them " +
                    "load instantly and background refresh right before your eyes! " +
                    "(You may need to throttle your network speed to simulate longer loading sequences)",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (postId > -1) {
            PostDetail(
                postId = postId,
                onBack = { postId = -1 },
                onVisited = { visitedPosts.add(it) }
            )
        } else {
            PostsList(
                onPostClick = { id -> postId = id },
                visitedPosts = visitedPosts
            )
        }
    }
}

@Composable
fun PostsList(
    onPostClick: (Int) -> Unit,
    visitedPosts: Set<Int>,
) {
    val queryState by useQuery<List<Post>> {
        httpClient.get("https://jsonplaceholder.typicode.com/posts").body()
    }

    Column {
        Text(
            text = "Posts",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = queryState.dataState) {
            is DataState.Pending -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...")
                }
            }

            is DataState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = Color.Red
                )
            }

            is DataState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.data) { post ->
                        PostItem(
                            post = post,
                            isVisited = visitedPosts.contains(post.id),
                            onClick = { onPostClick(post.id) }
                        )
                    }
                }

                if (queryState.fetchStatus == FetchStatus.Fetching) {
                    Text(
                        text = "Background Updating...",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    isVisited: Boolean,
    onClick: () -> Unit,
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = if (isVisited) Color.Green else Color.Blue,
                fontWeight = if (isVisited) FontWeight.Bold else FontWeight.Normal
            )
        ) {
            withLink(
                LinkAnnotation.Clickable(
                    tag = "post_${post.id}",
                    linkInteractionListener = { onClick() }
                )
            ) {
                append(post.title)
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun PostDetail(
    postId: Int,
    onBack: () -> Unit,
    onVisited: (Int) -> Unit,
) {
    LaunchedEffect(postId) {
        onVisited(postId)
    }

    val queryState by useQuery<Post> {
        httpClient.get("https://jsonplaceholder.typicode.com/posts/$postId").body()
    }

    Column {
        // Back button
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = queryState.dataState) {
            is DataState.Pending -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...")
                }
            }

            is DataState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = Color.Red
                )
            }

            is DataState.Success -> {
                Column {
                    Text(
                        text = state.data.title,
                        style = MaterialTheme.typography.h4,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = state.data.body,
                        style = MaterialTheme.typography.body1
                    )

                    if (queryState.fetchStatus == FetchStatus.Fetching) {
                        Text(
                            text = "Background Updating...",
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}