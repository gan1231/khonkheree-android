package app.khonkheree.data.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("auth")

@Singleton
class TokenStore @Inject constructor(@ApplicationContext private val ctx: Context) {

    private val KEY_ACCESS = stringPreferencesKey("access_token")
    private val KEY_REFRESH = stringPreferencesKey("refresh_token")

    val accessToken: String?
        get() = runBlocking { ctx.dataStore.data.first()[KEY_ACCESS] }

    val refreshToken: String?
        get() = runBlocking { ctx.dataStore.data.first()[KEY_REFRESH] }

    suspend fun save(access: String, refresh: String) {
        ctx.dataStore.edit {
            it[KEY_ACCESS] = access
            it[KEY_REFRESH] = refresh
        }
    }

    suspend fun clear() {
        ctx.dataStore.edit { it.clear() }
    }
}
