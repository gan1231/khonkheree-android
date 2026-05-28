package app.khonkheree.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.khonkheree.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthState(isLoggedIn = repo.isLoggedIn()))
    val state = _state.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        runCatching { repo.login(email, password) }
            .onSuccess { _state.update { it.copy(isLoading = false, isLoggedIn = true) } }
            .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        runCatching { repo.register(name, email, password) }
            .onSuccess { _state.update { it.copy(isLoading = false, isLoggedIn = true) } }
            .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
    }

    fun logout() = viewModelScope.launch {
        repo.logout()
        _state.update { it.copy(isLoggedIn = false) }
    }
}
