package com.desmond.codelab.core.di

import android.content.Context
import com.desmond.codelab.core.SettingsContext
import com.desmond.codelab.data.ApiService
import com.desmond.codelab.data.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context) = SettingsContext(context)

    @Singleton
    @Provides
    fun provideCoreRepository(service: ApiService) = MainRepository(service)


}