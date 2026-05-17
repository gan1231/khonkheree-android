package app.khonkheree.data.repository

import app.khonkheree.data.api.BookIn
import app.khonkheree.data.api.BookOut
import app.khonkheree.data.api.KhonkhereeApi
import app.khonkheree.data.api.ReviewIn
import app.khonkheree.data.api.ReviewOut
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: KhonkhereeApi) {

    suspend fun search(q: String? = null, status: String? = null, page: Int = 1): List<BookOut> =
        api.searchBooks(q, status, page)

    suspend fun myBooks(): List<BookOut> = api.myBooks()

    suspend fun getBook(id: String): BookOut = api.getBook(id)

    suspend fun addBook(book: BookIn): BookOut = api.addBook(book)

    suspend fun updateBook(id: String, book: BookIn): BookOut = api.updateBook(id, book)

    suspend fun deleteBook(id: String) = api.deleteBook(id)

    suspend fun getReviews(bookId: String): List<ReviewOut> = api.getReviews(bookId)

    suspend fun addReview(bookId: String, content: String, clientId: String): ReviewOut =
        api.addReview(bookId, ReviewIn(content = content, client_id = clientId))

    suspend fun toggleLike(reviewId: String): Map<String, Any> = api.toggleLike(reviewId)

    suspend fun identifyBook(imageBytes: ByteArray, fileName: String = "book.jpg") =
        api.identifyBook(
            MultipartBody.Part.createFormData(
                "image", fileName,
                imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )
        )
}
