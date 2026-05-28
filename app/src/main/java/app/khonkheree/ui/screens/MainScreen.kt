package app.khonkheree.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

sealed class NavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Library : NavTab("library", "Сан", Icons.Filled.Home, Icons.Outlined.Home)
    data object Search : NavTab("search", "Хайлт", Icons.Filled.Search, Icons.Outlined.Search)
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val tabs = listOf(NavTab.Library, NavTab.Search)
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val current by navController.currentBackStackEntryAsState()
                tabs.forEach { tab ->
                    val isSelected = current?.destination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label
                            ) 
                        },
                        label = { Text(tab.label) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = NavTab.Library.route) {
            composable(NavTab.Library.route) {
                LibraryScreen(
                    onBookClick = { id -> navController.navigate("book/$id") },
                    onAddBook = { /* TODO: AddBookScreen */ },
                )
            }
            composable(NavTab.Search.route) {
                SearchScreen(onBookClick = { id -> navController.navigate("book/$id") })
            }
            composable("book/{id}") { back ->
                val id = back.arguments?.getString("id") ?: return@composable
                BookDetailScreen(bookId = id, onBack = { navController.popBackStack() })
            }
        }
    }
}
