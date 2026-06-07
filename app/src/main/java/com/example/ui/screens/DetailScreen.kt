package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ui.theme.DisneyPromoGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: MainViewModel, navController: NavController, showName: String) {
    // Redefining selectShow inside LaunchedEffect as well to ensure correctness and synchronicity
    LaunchedEffect(showName) {
        viewModel.selectShow(showName)
    }

    val episodes = viewModel.selectedShowEpisodes.collectAsState().value
    val showDetailsMap = viewModel.showDetailsMap.collectAsState().value
    val details = showDetailsMap[showName]

    val posterUrl = details?.posterUrl?.ifEmpty { null } ?: episodes.firstOrNull()?.logoUrl

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(showName, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(360.dp)
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
                                    colors = listOf(
                                        Color.Transparent, 
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f), 
                                        MaterialTheme.colorScheme.background
                                    ),
                                    startY = 0f,
                                    endY = 1100f
                                )
                            )
                    )
                    
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = showName, 
                            style = MaterialTheme.typography.headlineLarge, 
                            fontWeight = FontWeight.Black, 
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                
                Column(Modifier.padding(horizontal = 16.dp)) {
                    // Quick Metadata row (Rating, year, genres)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!details?.rating.isNullOrEmpty() && details?.rating != "N/A") {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DisneyPromoGold.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "★ ${details?.rating}",
                                    color = DisneyPromoGold,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        
                        if (!details?.year.isNullOrEmpty() && details?.year != "N/A") {
                            Text(
                                text = details!!.year,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (!details?.genre.isNullOrEmpty() && details?.genre != "N/A") {
                            Text("•", color = Color.Gray)
                            Text(
                                text = details!!.genre,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Turkish Translated Plot Description
                    val plotText = if (!details?.plot.isNullOrEmpty()) {
                        details!!.plot
                    } else if (episodes.isNotEmpty()) {
                        "Harika çizgi film bölümlerini MoonToon kalitesiyle Türkçe seyredin!"
                    } else {
                        "Açıklama yükleniyor..."
                    }

                    Text(
                        text = plotText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Actors Information
                    if (!details?.actors.isNullOrEmpty() && details?.actors != "N/A") {
                        Text(
                            text = "Göz Atın / Oyuncular: ${details!!.actors}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Button(
                        onClick = { if (episodes.isNotEmpty()) navController.navigate("player/${episodes.first().id}") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                         Icon(Icons.Filled.PlayArrow, contentDescription = null)
                         Spacer(Modifier.width(8.dp))
                         Text("İLK BÖLÜMÜ İZLE", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }
                }
                
                Text(
                    text = "Bölümler (${episodes.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                )
            }

            items(episodes) { episode ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    onClick = { navController.navigate("player/${episode.id}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dynamic widescreen 16:9 preview for show episodes from m3u art, fallback to show poster
                        val itemCover = episode.logoUrl.ifEmpty { posterUrl ?: "" }
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .aspectRatio(16f/9f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = itemCover,
                                contentDescription = episode.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.PlayArrow, 
                                    contentDescription = null, 
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = episode.title, 
                                style = MaterialTheme.typography.titleMedium, 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = episode.category, 
                                style = MaterialTheme.typography.bodySmall, 
                                color = Color.LightGray
                            )
                        }
                        IconButton(onClick = { viewModel.toggleFavorite(episode.id, episode.isFavorite) }) {
                            Icon(
                                if (episode.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (episode.isFavorite) MaterialTheme.colorScheme.primary else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
