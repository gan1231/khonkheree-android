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

data class LibraryScreenState(
    val books: List<BookOut> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedBook: BookOut? = null,
)

@HiltViewModel
class LibraryViewModel @Inject constructor(private val repo: BookRepository) : ViewModel() {

    private val _state = MutableStateFlow(LibraryScreenState())
    val state = _state.asStateFlow()

    fun loadMyBooks() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        runCatching { repo.myBooks() }
            .onSuccess { books ->
                _state.update { it.copy(books = books, isLoading = false) }
            }
            .onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message ?: "Алдаа: Номуудыг ачаалж чадсангүй") }
            }
    }

    fun deleteBook(id: String) = viewModelScope.launch {
        runCatching { repo.deleteBook(id) }
            .onSuccess { loadMyBooks() }
            .onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Номыг устгаж чадсангүй") }
            }
    }

    fun selectBook(book: BookOut) {
        _state.update { it.copy(selectedBook = book) }
    }

    fun clearSelection() {
        _state.update { it.copy(selectedBook = null) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
