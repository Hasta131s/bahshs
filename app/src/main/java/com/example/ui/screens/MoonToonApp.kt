package com.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.data.AppContainer

@Composable
fun MoonToonApp(viewModel: MainViewModel, appContainer: AppContainer) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") },
                    icon = { Text("🏠") },
                    label = { Text("Ana Sayfa") }
                )
                NavigationBarItem(
                    selected = currentRoute == "search",
                    onClick = { navController.navigate("search") },
                    icon = { Text("🔍") },
                    label = { Text("Ara") }
                )
                NavigationBarItem(
                    selected = currentRoute == "favorites",
                    onClick = { navController.navigate("favorites") },
                    icon = { Text("❤️") },
                    label = { Text("Favoriler") }
                )
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = { navController.navigate("profile") },
                    icon = { Text("👤") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(viewModel = viewModel, navController = navController)
            }
            composable("search") {
                SearchScreen(viewModel = viewModel, navController = navController)
            }
            composable("favorites") {
                FavoritesScreen(viewModel = viewModel, navController = navController)
            }
            composable("profile") {
                ProfileScreen(navController = navController)
            }
            composable(
                "details/{showName}",
                arguments = listOf(navArgument("showName") { type = NavType.StringType })
            ) { backStackEntry ->
                val showName = backStackEntry.arguments?.getString("showName") ?: ""
                DetailScreen(
                    showName = showName,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(
                "player/{episodeId}",
                arguments = listOf(navArgument("episodeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val episodeId = backStackEntry.arguments?.getString("episodeId") ?: ""
                PlayerScreen(
                    episodeId = episodeId,
                    appContainer = appContainer,
                    navController = navController
                )
            }
        }
    }
}
