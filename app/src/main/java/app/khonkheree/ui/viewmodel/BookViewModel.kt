package app.khonkheree.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.khonkheree.data.api.BookOut
import app.khonkheree.data.api.ReviewOut
import app.khonkheree.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryState(
    val books: List<BookOut> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class BookDetailState(
    val book: BookOut? = null,
    val reviews: List<ReviewOut> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class BookViewModel @Inject constructor(private val repo: BookRepository) : ViewModel() {

    private val _library = MutableStateFlow(LibraryState())
    val library = _library.asStateFlow()

    private val _search = MutableStateFlow(LibraryState())
    val search = _search.asStateFlow()

    private val _detail = MutableStateFlow(BookDetailState())
    val detail = _detail.asStateFlow()

    fun loadMyBooks() = viewModelScope.launch {
        _library.update { it.copy(isLoading = true, error = null) }
        runCatching { repo.myBooks() }
            .onSuccess { books -> _library.update { it.copy(books = books, isLoading = false) } }
            .onFailure { e -> _library.update { it.copy(isLoading = false, error = e.message) } }
    }

    fun searchBooks(q: String, status: String? = null) = viewModelScope.launch {
        _search.update { it.copy(isLoading = true, error = null) }
        runCatching { repo.search(q.ifBlank { null }, status) }
            .onSuccess { books -> _search.update { it.copy(books = books, isLoading = false) } }
            .onFailure { e -> _search.update { it.copy(isLoading = false, error = e.message) } }
    }

    fun loadPublicBooks() = viewModelScope.launch {
        _search.update { it.copy(isLoading = true, error = null) }
        runCatching { repo.search() }
            .onSuccess { books -> _search.update { it.copy(books = books, isLoading = false) } }
            .onFailure { e -> _search.update { it.copy(isLoading = false, error = e.message) } }
    }

    fun loadBookDetail(id: String) = viewModelScope.launch {
        _detail.update { it.copy(isLoading = true, error = null) }
        runCatching {
            val book = repo.getBook(id)
            val reviews = repo.getReviews(id)
            Pair(book, reviews)
        }.onSuccess { (book, reviews) ->
            _detail.update { it.copy(book = book, reviews = reviews, isLoading = false) }
        }.onFailure { e ->
            _detail.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    fun deleteBook(id: String) = viewModelScope.launch {
        runCatching { repo.deleteBook(id) }.onSuccess { loadMyBooks() }
    }

    fun addReview(bookId: String, content: String) = viewModelScope.launch {
        val clientId = java.util.UUID.randomUUID().toString()
        runCatching { repo.addReview(bookId, content, clientId) }
            .onSuccess { loadBookDetail(bookId) }
    }

    fun toggleLike(reviewId: String) = viewModelScope.launch {
        runCatching { repo.toggleLike(reviewId) }
    }
}
