package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.ShowInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController) {
    val allShows = viewModel.allShows.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MoonToon", color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "Önerilenler",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allShows.take(5)) { show ->
                        ShowItem(show) {
                            navController.navigate("details/${show.showName}")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Tüm Çizgi Diziler",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(allShows) { show ->
                ShowItemRow(show) {
                    navController.navigate("details/${show.showName}")
                }
            }
        }
    }
}

@Composable
fun ShowItem(show: ShowInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        Column {
            AsyncImage(
                model = show.logoUrl,
                contentDescription = show.showName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Text(
                show.showName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = show.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ShowItemRow(show: ShowInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(Modifier.height(100.dp)) {
            AsyncImage(
                model = show.logoUrl,
                contentDescription = show.showName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(100.dp).fillMaxHeight()
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(show.showName, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(show.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
