package app.khonkheree.ui

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

// ─── App-wide state for navigation and user session ─────────────────────────

data class AppState(
    val navController: NavHostController,
    val isLoggedIn: Boolean = false,
    val currentRoute: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    fun navigateTo(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    fun clearError() {
        // Error state management - to be used in Composable functions
    }
}

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
): AppState {
    return remember(navController) {
        AppState(navController = navController)
    }
}

// ─── State hoisting for AppState ─────────────────────────────────────────

@Composable
fun <T> rememberMutableState(initialValue: T): MutableState<T> {
    return remember { mutableStateOf(initialValue) }
}

@Composable
fun <T> rememberMutableStateOf(
    initialValue: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
): MutableState<T> {
    return remember { mutableStateOf(initialValue, policy) }
}
