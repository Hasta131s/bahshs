package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ui.theme.RedMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController) {
    val allShows = viewModel.allShows.collectAsState().value
    val history = viewModel.history.collectAsState().value
    
    val showsByCategory = allShows.groupBy { it.category }
    val featuredShow = allShows.firstOrNull { it.showName.contains("Adventure Time", true) } ?: allShows.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MoonToon",
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleLarge,
                            color = RedMain
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Filled.Search, contentDescription = "Ara", tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color(0xFF0F0F13) // Deep dark background mode
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F13))
        ) {
            // HERO BANNER
            if (featuredShow != null) {
                item {
                    androidx.compose.runtime.LaunchedEffect(featuredShow.showName) {
                        viewModel.fetchOmdbDetails(featuredShow.showName)
                    }
                    val details = viewModel.omdbDetails.collectAsState().value[featuredShow.showName]
                    val posterUrl = details?.poster?.takeIf { it.isNotEmpty() } ?: featuredShow.logoUrl
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp)
                            .clickable { navController.navigate("details/${featuredShow.showName}") }
                    ) {
                        AsyncImage(
                            model = posterUrl,
                            contentDescription = featuredShow.showName,
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
                                            Color(0xFF0F0F13).copy(alpha = 0.5f),
                                            Color(0xFF0F0F13)
                                        ),
                                        startY = 0f,
                                        endY = 1300f
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = "YENİ BÖLÜM",
                                    color = RedMain,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black
                                )
                                Text("•", color = Color.White)
                                Text(
                                    text = featuredShow.category,
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Text(
                                text = featuredShow.showName,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 16.dp),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { navController.navigate("details/${featuredShow.showName}") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Hemen İzle", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                 item { Spacer(modifier = Modifier.height(padding.calculateTopPadding() + 24.dp)) }
            }

            // CONTINUE WATCHING
            if (history.isNotEmpty()) {
                item {
                    CategoryHeader("İzlemeye Devam Et")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(history.take(8)) { episode ->
                            Column(
                                modifier = Modifier
                                    .width(160.dp)
                                    .clickable { navController.navigate("player/${episode.id}") }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f/9f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.DarkGray)
                                ) {
                                    AsyncImage(
                                        model = episode.logoUrl,
                                        contentDescription = episode.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    val progress = if (episode.totalDuration > 0) episode.watchProgress.toFloat() / episode.totalDuration else 0f
                                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.White.copy(0.3f)).align(Alignment.BottomCenter)) {
                                        Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(RedMain))
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = episode.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = episode.showName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // CATEGORIES CAROUSEL
            showsByCategory.forEach { (catName, shows) ->
                item {
                    CategoryHeader(catName.ifEmpty { "Diğer" })
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(shows) { show ->
                            ShowCard(show = show, viewModel = viewModel, navController = navController)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
            
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ShowCard(show: ShowInfo, viewModel: MainViewModel, navController: NavController) {
    androidx.compose.runtime.LaunchedEffect(show.showName) {
        viewModel.fetchOmdbDetails(show.showName)
    }
    val details = viewModel.omdbDetails.collectAsState().value[show.showName]
    val posterUrl = details?.poster?.takeIf { it.isNotEmpty() } ?: show.logoUrl

    Box(
        modifier = Modifier
            .width(120.dp)
            .aspectRatio(2f/3f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { navController.navigate("details/${show.showName}") }
            .background(Color.DarkGray)
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = show.showName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha=0.9f)),
                startY = 100f
            )
        ))
        Text(
            text = show.showName,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
