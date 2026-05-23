package app.khonkheree.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.khonkheree.data.api.BookOut
import app.khonkheree.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchScreenState(
    val books: List<BookOut> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val searchStatus: String? = null,
    val currentPage: Int = 1,
)

@HiltViewModel
class SearchViewModel @Inject constructor(private val repo: BookRepository) : ViewModel() {

    private val _state = MutableStateFlow(SearchScreenState())
    val state = _state.asStateFlow()

    fun loadPublicBooks(page: Int = 1) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null, currentPage = page) }
        runCatching { repo.search(page = page) }
            .onSuccess { books ->
                _state.update { it.copy(books = books, isLoading = false) }
            }
            .onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message ?: "Публик номуудыг ачаалж чадсангүй") }
            }
    }

    fun searchBooks(q: String, status: String? = null, page: Int = 1) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                query = q,
                searchStatus = status,
                currentPage = page
            )
        }
        runCatching { repo.search(q.ifBlank { null }, status, page) }
            .onSuccess { books ->
                _state.update { it.copy(books = books, isLoading = false) }
            }
            .onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message ?: "Номуудыг хайж чадсангүй") }
            }
    }

    fun clearSearch() {
        _state.update { it.copy(query = "", books = emptyList()) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
