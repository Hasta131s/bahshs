package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MainViewModel, navController: NavController) {
    val query = viewModel.searchQuery.collectAsState().value
    val results = viewModel.searchResults.collectAsState().value

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Arama...") }
        )

        LazyColumn(Modifier.fillMaxSize()) {
            items(results) { show ->
                ShowItemRow(show = show) {
                    navController.navigate("details/${show.showName}")
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(viewModel: MainViewModel, navController: NavController) {
    val favorites = viewModel.favorites.collectAsState().value

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Favori Bölümlerim", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(favorites) { media ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    onClick = { navController.navigate("player/${media.id}") }
                ) {
                    Row(Modifier.padding(16.dp)) {
                        Text(media.showName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                        Text(media.title)
                    }
                }
            }
        }
    }
}
