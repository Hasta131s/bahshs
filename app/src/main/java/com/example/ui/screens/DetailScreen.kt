package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ui.theme.RedMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(showName: String, viewModel: MainViewModel, navController: NavController) {
    LaunchedEffect(showName) {
        viewModel.loadShowDetails(showName)
    }

    val details = viewModel.selectedShowDetails.collectAsState().value
    val episodes = viewModel.selectedShowEpisodes.collectAsState().value
    
    val posterUrl = episodes.firstOrNull()?.logoUrl

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(showName, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                ) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                    startY = 100f
                                )
                            )
                    )
                    
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(details?.Title ?: showName, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = Color.White)
                        details?.let {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(it.Year ?: "", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.width(8.dp))
                                Box(modifier = Modifier.background(Color.White.copy(alpha=0.2f), RoundedCornerShape(4.dp)).padding(horizontal=4.dp)) {
                                     Text("IMDb ${it.imdbRating}", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
                
                details?.let {
                    Column(Modifier.padding(16.dp)) {
                        Text(it.Plot ?: "Konu bulunamadı.", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Spacer(Modifier.height(16.dp))
                        
                        Button(
                            onClick = { if (episodes.isNotEmpty()) navController.navigate("player/${episodes.first().id}") },
                            colors = ButtonDefaults.buttonColors(containerColor = RedMain),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                             Icon(Icons.Filled.PlayArrow, contentDescription = null)
                             Spacer(Modifier.width(8.dp))
                             Text("İlk Bölümü İzle", fontWeight = FontWeight.Bold)
                        }
                    }
                } ?: run {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RedMain)
                    }
                }
                
                Text(
                    text = "Bölümler (${episodes.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(episodes) { episode ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { navController.navigate("player/${episode.id}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                             Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(episode.title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(episode.category, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }
                        IconButton(onClick = { viewModel.toggleFavorite(episode.id, episode.isFavorite) }) {
                            Icon(
                                if (episode.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (episode.isFavorite) RedMain else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
