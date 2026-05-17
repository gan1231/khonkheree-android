package app.khonkheree.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

sealed class NavTab(val route: String, val label: String, val icon: ImageVector) {
    data object Library : NavTab("library", "Номын сан", Icons.Default.Person)
    data object Search : NavTab("search", "Хайлт", Icons.Default.Search)
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val tabs = listOf(NavTab.Library, NavTab.Search)
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val current by navController.currentBackStackEntryAsState()
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, tab.label) },
                        label = { Text(tab.label) },
                        selected = current?.destination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { _ ->
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
