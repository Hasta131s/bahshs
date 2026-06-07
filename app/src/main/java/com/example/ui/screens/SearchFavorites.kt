package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ui.theme.RedMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MainViewModel, navController: NavController) {
    val query = viewModel.searchQuery.collectAsState().value
    val results = viewModel.searchResults.collectAsState().value
    val showDetailsMap = viewModel.showDetailsMap.collectAsState().value

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Arama", fontWeight = FontWeight.Bold, color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Dizi veya kategori ara...") },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent
            )
        )

        LazyColumn(Modifier.fillMaxSize()) {
            items(results) { media ->
                val details = showDetailsMap[media.showName]
                val posterUrl = details?.posterUrl?.ifEmpty { null } ?: media.logoUrl
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = { navController.navigate("details/${media.showName}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!posterUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = posterUrl,
                                contentDescription = media.showName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp, 75.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(Modifier.width(16.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = media.showName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = media.category,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(viewModel: MainViewModel, navController: NavController) {
    val favorites = viewModel.favorites.collectAsState().value
    val showDetailsMap = viewModel.showDetailsMap.collectAsState().value

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Favorilerim", fontWeight = FontWeight.Bold, color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(favorites) { media ->
                val details = showDetailsMap[media.showName]
                val posterUrl = details?.posterUrl?.ifEmpty { null } ?: media.logoUrl
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    onClick = { navController.navigate("player/${media.id}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!posterUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = posterUrl,
                                contentDescription = media.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp, 75.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(Modifier.width(16.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = media.showName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = media.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: MainViewModel, navController: NavController) {
    val history = viewModel.history.collectAsState().value
    val showDetailsMap = viewModel.showDetailsMap.collectAsState().value

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("İzleme Geçmişi", fontWeight = FontWeight.Bold, color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(history) { media ->
                val details = showDetailsMap[media.showName]
                val posterUrl = details?.posterUrl?.ifEmpty { null } ?: media.logoUrl
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    onClick = { navController.navigate("player/${media.id}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!posterUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = posterUrl,
                                contentDescription = media.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp, 75.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(Modifier.width(16.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = media.showName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = media.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.LightGray
                            )
                            val progress = if (media.totalDuration > 0) media.watchProgress.toFloat() / media.totalDuration else 0f
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
