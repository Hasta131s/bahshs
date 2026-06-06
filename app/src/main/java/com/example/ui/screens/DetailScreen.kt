package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(showName: String, viewModel: MainViewModel, navController: NavController) {
    LaunchedEffect(showName) {
        viewModel.loadShowDetails(showName)
    }

    val details = viewModel.selectedShowDetails.collectAsState().value
    val episodes = viewModel.selectedShowEpisodes.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(showName) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                AsyncImage(
                    model = episodes.firstOrNull()?.logoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
                
                details?.let {
                    Column(Modifier.padding(16.dp)) {
                        Text(it.Title ?: "", style = MaterialTheme.typography.headlineMedium)
                        Text("Yıl: ${it.Year} | IMDB: ${it.imdbRating}", color = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.height(8.dp))
                        Text(it.Plot ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                } ?: run {
                    Box(Modifier.fillMaxWidth().padding(16.dp)) {
                        CircularProgressIndicator()
                    }
                }
                
                Text(
                    text = "Bölümler (${episodes.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(episodes) { episode ->
                ListItem(
                    headlineContent = { Text(episode.title) },
                    supportingContent = { Text(episode.category) },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { viewModel.toggleFavorite(episode.id, episode.isFavorite) }) {
                                Icon(
                                    if (episode.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favori",
                                    tint = if (episode.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = { navController.navigate("player/${episode.id}") }) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "İzle")
                            }
                        }
                    }
                )
                Divider()
            }
        }
    }
}
