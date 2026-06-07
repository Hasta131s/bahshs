package com.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.data.AppContainer

@Composable
fun MoonToonApp(appContainer: AppContainer) {
    val context = LocalContext.current
    val viewModel: MainViewModel = remember { MainViewModel(appContainer, context) }
    val navController = rememberNavController()

    val currentRouteForPadding = navController.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = if (currentRouteForPadding?.startsWith("player/") == true) WindowInsets(0, 0, 0, 0) else WindowInsets.systemBars,
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute?.startsWith("player/") != true) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Ana Sayfa") },
                        label = { Text("Ana Sayfa", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "search",
                        onClick = { navController.navigate("search") },
                        icon = { Icon(Icons.Filled.Search, contentDescription = "Ara") },
                        label = { Text("Ara", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "favorites",
                        onClick = { navController.navigate("favorites") },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoriler") },
                        label = { Text("Favoriler", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") },
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Profil") },
                        label = { Text("Profil", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        val currentRouteForPadding = navController.currentBackStackEntryAsState().value?.destination?.route
        val modifier = if (currentRouteForPadding?.startsWith("player/") == true) Modifier else Modifier.padding(padding)
        NavHost(navController = navController, startDestination = "home", modifier = modifier) {
            composable("home") {
                HomeScreen(viewModel = viewModel, navController = navController)
            }
            composable("search") {
                SearchScreen(viewModel = viewModel, navController = navController)
            }
            composable("favorites") {
                FavoritesScreen(viewModel = viewModel, navController = navController)
            }
            composable("history") {
                HistoryScreen(viewModel = viewModel, navController = navController)
            }
            composable("profile") {
                ProfileScreen(navController = navController)
            }
            composable("details/{showName}") { backStackEntry ->
                val showName = backStackEntry.arguments?.getString("showName") ?: ""
                viewModel.selectShow(showName)
                DetailScreen(viewModel = viewModel, navController = navController, showName = showName)
            }
            composable("player/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                PlayerScreen(episodeId = id, appContainer = appContainer, navController = navController)
            }
        }
    }
}
