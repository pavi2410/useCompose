package com.pavi2410.useCompose.demo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pavi2410.useCompose.demo.common.httpClient
import com.pavi2410.useCompose.query.DataState
import com.pavi2410.useCompose.query.core.Key
import com.pavi2410.useCompose.query.core.QueryOptions
import com.pavi2410.useCompose.query.useQuery
import com.pavi2410.useCompose.query.useQueryClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class CharactersResponse(
    val results: List<Character>,
)

@Serializable
data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val origin: CharacterLocation,
    val location: CharacterLocation,
    val image: String,
)

@Serializable
data class CharacterLocation(
    val name: String,
    val url: String,
)

data class CharactersKey(val type: String = "all") : Key
data class CharacterKey(val characterId: Int) : Key

suspend fun getCharacters(): CharactersResponse {
    return httpClient.get("https://rickandmortyapi.com/api/character/").body()
}

suspend fun getCharacter(characterId: Int): Character {
    return httpClient.get("https://rickandmortyapi.com/api/character/$characterId").body()
}

@Composable
fun PrefetchingExample(modifier: Modifier = Modifier) {
    val queryClient = useQueryClient()
    val coroutineScope = rememberCoroutineScope()
    var selectedCharId by remember { mutableIntStateOf(1) }
    var rerender by remember { mutableIntStateOf(0) } // Force recomposition trigger

    val charactersQuery by useQuery(
        key = CharactersKey(),
        queryFn = { getCharacters() }
    )

    val characterQuery by useQuery(
        key = CharacterKey(selectedCharId),
        queryFn = { getCharacter(selectedCharId) }
    )

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Hovering over a character will prefetch it, and when it's been " +
                    "prefetched it will turn bold. Clicking on a prefetched character " +
                    "will show their stats on the right immediately.",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            // Left half - Character List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "Characters",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when (val state = charactersQuery.dataState) {
                    is DataState.Pending -> {
                        Text("Loading...")
                    }

                    is DataState.Error -> {
                        Text("Error: ${state.message}", color = Color.Red)
                    }

                    is DataState.Success -> {
                        LazyColumn {
                            items(state.data.results) { character ->
                                CharacterItemWithCache(
                                    character = character,
                                    rerender = rerender,
                                    queryClient = queryClient,
                                    isSelected = character.id == selectedCharId,
                                    onHover = {
                                        coroutineScope.launch {
                                            queryClient.prefetchQuery(
                                                key = CharacterKey(character.id),
                                                queryFn = { getCharacter(character.id) },
                                                options = QueryOptions(staleTime = 10 * 1000) // 10 seconds
                                            )
                                            // Trigger recomposition to update bold styling
                                            delay(1)
                                            rerender++
                                        }
                                    },
                                    onClick = {
                                        selectedCharId = character.id
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Right half - Character Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Selected Character",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when (val charState = characterQuery.dataState) {
                    is DataState.Pending -> {
                        Text("Loading...")
                    }

                    is DataState.Error -> {
                        Text("Error: ${charState.message}", color = Color.Red)
                    }

                    is DataState.Success -> {
                        CharacterDetails(charState.data)
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterItemWithCache(
    character: Character,
    rerender: Int, // Used as a key to trigger cache re-checks
    queryClient: com.pavi2410.useCompose.query.core.QueryClient,
    isSelected: Boolean = false,
    onHover: () -> Unit,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    var isPrefetched by remember { mutableStateOf(false) }

    // Check cache status when rerender changes
    LaunchedEffect(character.id, rerender) {
        val cachedData = queryClient.getQueryData<Character>(CharacterKey(character.id))
        isPrefetched = cachedData != null
    }

    LaunchedEffect(isHovered) {
        if (isHovered) {
            onHover()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = when {
                    isSelected -> MaterialTheme.colors.primary.copy(alpha = 0.2f)
                    isHovered -> Color.Gray.copy(alpha = 0.1f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(4.dp)
            )
            .hoverable(interactionSource = interactionSource)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            text = "${character.id} - ${character.name}",
            fontWeight = if (isPrefetched) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colors.primary else Color.Unspecified
        )
    }
}

@Composable
fun CharacterDetails(character: Character) {
    Column {
        Text(
            text = "Name: ${character.name}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "Status: ${character.status}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "Species: ${character.species}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "Gender: ${character.gender}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "Origin: ${character.origin.name}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "Location: ${character.location.name}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}