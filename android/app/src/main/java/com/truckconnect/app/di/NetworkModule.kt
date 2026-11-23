package com.truckconnect.app.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.truckconnect.app.BuildConfig
import com.truckconnect.core.data.TokenManager
import com.truckconnect.core.network.AuthInterceptor
import com.truckconnect.core.network.api.AuthApiService
import com.truckconnect.core.network.api.DirectoryApiService
import com.truckconnect.core.network.api.ShipmentApiService
import com.truckconnect.core.network.api.TrackingApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { tokenManager.getAccessToken() })
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideShipmentApiService(retrofit: Retrofit): ShipmentApiService {
        return retrofit.create(ShipmentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDirectoryApiService(retrofit: Retrofit): DirectoryApiService {
        return retrofit.create(DirectoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTrackingApiService(retrofit: Retrofit): TrackingApiService {
        return retrofit.create(TrackingApiService::class.java)
    }
}
