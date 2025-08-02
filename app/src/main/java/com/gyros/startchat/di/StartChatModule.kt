package com.gyros.startchat.di

import android.content.Context
import com.gyros.startchat.data.ClipBoardManager
import com.gyros.startchat.data.ClipBoardManagerImpl
import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.repositories.CountryCodeRepository
import com.gyros.startchat.repositories.CountryCodeRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object StartChatModule {
    @Provides
    fun provideCountryCodesReader(@ApplicationContext context: Context): CountryCodesReader {
        return CountryCodesReader(context, "country_codes.json", Moshi.Builder().add(
            KotlinJsonAdapterFactory()
        ).build())
    }

    @Provides
    fun provideCountryCodeRepository(countryCodeRepository: CountryCodeRepositoryImpl): CountryCodeRepository {
        return countryCodeRepository
    }

    @Provides
    fun provideClipBoardManager(@ApplicationContext context: Context): ClipBoardManager {
        return ClipBoardManagerImpl(context)
    }

}