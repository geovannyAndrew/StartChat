package com.gyros.startchat.di

import android.content.Context
import com.gyros.startchat.data.AppInfo
import com.gyros.startchat.data.AppInfoImpl
import com.gyros.startchat.data.ClipBoardManager
import com.gyros.startchat.data.ClipBoardManagerImpl
import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.data.CountryCodesReaderImpl
import com.gyros.startchat.data.UrlOpener
import com.gyros.startchat.data.UrlOpenerImpl
import com.gyros.startchat.repositories.ChatHistoryRepository
import com.gyros.startchat.repositories.ChatHistoryRepositoryImpl
import com.gyros.startchat.repositories.CountryCodeRepository
import com.gyros.startchat.repositories.CountryCodeRepositoryImpl
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
        return CountryCodesReaderImpl(context, "country_codes.json")
    }

    @Provides
    fun provideCountryCodeRepository(countryCodeRepository: CountryCodeRepositoryImpl): CountryCodeRepository {
        return countryCodeRepository
    }

    @Provides
    fun provideClipBoardManager(@ApplicationContext context: Context): ClipBoardManager {
        return ClipBoardManagerImpl(context)
    }

    @Provides
    fun provideChatHistoryRepository(impl: ChatHistoryRepositoryImpl): ChatHistoryRepository = impl

    @Provides
    fun provideUrlOpener(@ApplicationContext context: Context): UrlOpener {
        return UrlOpenerImpl(context)
    }

    @Provides
    fun provideAppInfo(@ApplicationContext context: Context): AppInfo {
        return AppInfoImpl(context)
    }
}