package com.desmond.codelab.core.di

import com.desmond.codelab.BuildConfig
import com.desmond.codelab.core.AppExecutor
import com.desmond.codelab.core.SettingsContext
import com.desmond.codelab.data.ApiService
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val gson by lazy {
        GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(settingContext: SettingsContext): OkHttpClient {

        val httpClient = OkHttpClient.Builder()
        val apiInterceptor = HttpLoggingInterceptor()
        apiInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(apiInterceptor)
        }
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .cache(null)
        return httpClient.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(appExecutor: AppExecutor, client: OkHttpClient): Retrofit = Retrofit.Builder()
        // .baseUrl(BuildConfig)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .callbackExecutor(appExecutor.networkExecutor)
        .build()

    @Singleton
    @Provides
    fun provideCoreWebService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}