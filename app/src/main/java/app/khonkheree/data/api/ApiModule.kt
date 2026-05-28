package app.khonkheree.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "http://10.0.2.2:8000/api/v1/"

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides @Singleton
    fun provideOkHttp(tokenStore: TokenStore): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = tokenStore.accessToken
                val req = if (token != null)
                    chain.request().newBuilder().header("Authorization", "Bearer $token").build()
                else chain.request()
                chain.proceed(req)
            }
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

    @Provides @Singleton
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): KhonkhereeApi =
        retrofit.create(KhonkhereeApi::class.java)
}
