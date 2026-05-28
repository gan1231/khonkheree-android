package app.khonkheree.data.repository

import app.khonkheree.data.api.KhonkhereeApi
import app.khonkheree.data.api.LoginRequest
import app.khonkheree.data.api.RegisterRequest
import app.khonkheree.data.api.TokenStore
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: KhonkhereeApi,
    private val tokenStore: TokenStore,
) {
    suspend fun login(email: String, password: String) {
        val tokens = api.login(LoginRequest(email, password))
        tokenStore.save(tokens.access_token, tokens.refresh_token)
    }

    suspend fun register(name: String, email: String, password: String) {
        val tokens = api.register(RegisterRequest(name, email, password))
        tokenStore.save(tokens.access_token, tokens.refresh_token)
    }

    suspend fun logout() = tokenStore.clear()
    fun isLoggedIn() = tokenStore.accessToken != null
}
