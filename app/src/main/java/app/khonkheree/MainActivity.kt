package app.khonkheree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import app.khonkheree.ui.screens.LoginScreen
import app.khonkheree.ui.screens.MainScreen
import app.khonkheree.ui.theme.KhonkhereeTheme
import app.khonkheree.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KhonkhereeTheme {
                val authVm: AuthViewModel = hiltViewModel()
                val state by authVm.state.collectAsState()
                if (state.isLoggedIn) {
                    MainScreen(onLogout = { authVm.logout() })
                } else {
                    LoginScreen(onLoginSuccess = {})
                }
            }
        }
    }
}
