package app.khonkheree.data.api

import okhttp3.MultipartBody
import retrofit2.http.*

// ─── Data classes ─────────────────────────────────────────────────────────────

data class TokenResponse(
    val access_token: String,
    val refresh_token: String,
    val token_type: String,
)

data class RegisterRequest(val name: String, val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class RefreshRequest(val refresh_token: String)

data class BookOut(
    val id: String,
    val user_id: String,
    val title: String,
    val author: String,
    val isbn: String?,
    val cover_url: String?,
    val synopsis: String?,
    val status: String,
    val sale_price: Double?,
    val is_public: Boolean,
    val added_date: String,
    val client_id: String,
    val review_count: Int,
)

data class BookIn(
    val title: String,
    val author: String,
    val isbn: String? = null,
    val cover_url: String? = null,
    val synopsis: String? = null,
    val status: String = "owned",
    val sale_price: Double? = null,
    val is_public: Boolean = true,
    val client_id: String,
)

data class ReviewOut(
    val id: String,
    val book_id: String,
    val user_id: String?,
    val author_name: String,
    val is_anonymous: Boolean,
    val content: String,
    val likes_count: Int,
    val created_at: String,
)

data class ReviewIn(
    val content: String,
    val author_name: String = "Нэргүй",
    val is_anonymous: Boolean = true,
    val client_id: String,
)

data class BookIdentifyResult(
    val isbn: String?,
    val title: String?,
    val author: String?,
    val synopsis: String?,
    val cover_url: String?,
    val source: String,
    val confidence: Double,
)

// ─── API Interface ─────────────────────────────────────────────────────────────

interface KhonkhereeApi {

    // Auth
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): TokenResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): TokenResponse

    // Books
    @GET("books")
    suspend fun searchBooks(
        @Query("q") q: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
    ): List<BookOut>

    @GET("books/mine")
    suspend fun myBooks(): List<BookOut>

    @GET("books/{id}")
    suspend fun getBook(@Path("id") id: String): BookOut

    @POST("books")
    suspend fun addBook(@Body body: BookIn): BookOut

    @PUT("books/{id}")
    suspend fun updateBook(@Path("id") id: String, @Body body: BookIn): BookOut

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") id: String)

    // Reviews
    @GET("books/{bookId}/reviews")
    suspend fun getReviews(@Path("bookId") bookId: String): List<ReviewOut>

    @POST("books/{bookId}/reviews")
    suspend fun addReview(@Path("bookId") bookId: String, @Body body: ReviewIn): ReviewOut

    @POST("reviews/{id}/like")
    suspend fun toggleLike(@Path("id") id: String): Map<String, Any>

    // ML
    @Multipart
    @POST("ml/identify-book")
    suspend fun identifyBook(@Part image: MultipartBody.Part): BookIdentifyResult
}
