package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.theme.RedMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MainViewModel, navController: NavController) {
    val query = viewModel.searchQuery.collectAsState().value
    val results = viewModel.searchResults.collectAsState().value

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
                focusedBorderColor = RedMain,
                unfocusedBorderColor = Color.Transparent
            )
        )

        LazyColumn(Modifier.fillMaxSize()) {
            items(results) { media ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = { navController.navigate("details/${media.showName}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(media.showName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(media.category, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
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

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Favorilerim", fontWeight = FontWeight.Bold, color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(favorites) { media ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    onClick = { navController.navigate("player/${media.id}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(16.dp)) {
                        Column {
                            Text(media.showName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(media.title, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
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

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("İzleme Geçmişi", fontWeight = FontWeight.Bold, color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(history) { media ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    onClick = { navController.navigate("player/${media.id}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(media.showName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(media.title, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                        val progress = if (media.totalDuration > 0) media.watchProgress.toFloat() / media.totalDuration else 0f
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth(), color = RedMain)
                    }
                }
            }
        }
    }
}
