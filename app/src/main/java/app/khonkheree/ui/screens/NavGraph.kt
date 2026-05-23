package app.khonkheree.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.graphics.shapes.CornerBasedShape
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.graphics.vector.ImageVector

// ─── Navigation Routes ─────────────────────────────────────────────────────

sealed class NavDestination(val route: String) {
    data object Auth : NavDestination("auth")
    data object Login : NavDestination("login")
    data object Register : NavDestination("register")
    data object Home : NavDestination("home")
    data object Library : NavDestination("library")
    data object Search : NavDestination("search")
    data object Profile : NavDestination("profile")
    data object BookDetail : NavDestination("book/{id}")
    data object AddBook : NavDestination("add_book")
}

data class NavTab(val route: String, val label: String, val icon: ImageVector) {
    companion object {
        fun library() = NavTab("library", "Номын сан", Icons.Default.Book)
        fun search() = NavTab("search", "Хайлт", Icons.Default.Search)
        fun profile() = NavTab("profile", "Профайл", Icons.Default.Person)
    }
}

// ─── NavGraph Composable ──────────────────────────────────────────────────

@Composable
fun NavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
) {
    val startDestination = if (isLoggedIn) NavDestination.Home.route else NavDestination.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // Auth routes
        composable(NavDestination.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavDestination.Home.route) {
                        popUpTo(NavDestination.Login.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavDestination.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavDestination.Home.route) {
                        popUpTo(NavDestination.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                },
            )
        }

        // Main routes
        composable(NavDestination.Home.route) {
            MainScreen(
                onLogout = {
                    onLogout()
                    navController.navigate(NavDestination.Login.route) {
                        popUpTo(NavDestination.Home.route) { inclusive = true }
                    }
                },
            )
        }
    }
}

// ─── Bottom Navigation ─────────────────────────────────────────────────────

@Composable
fun BottomNavBar(
    navController: NavHostController,
    tabs: List<NavTab>,
) {
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
