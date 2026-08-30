package com.gyros.startchat.di

import com.gyros.startchat.data.AppInfo
import com.gyros.startchat.data.AppInfoImpl
import com.gyros.startchat.data.AppSettings
import com.gyros.startchat.data.ClipBoardManager
import com.gyros.startchat.data.ClipBoardManagerImpl
import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.data.CountryCodesReaderImpl
import com.gyros.startchat.data.SettingsImpl
import com.gyros.startchat.data.UrlOpener
import com.gyros.startchat.data.UrlOpenerImpl
import com.gyros.startchat.domain.GetChatHistoryUseCase
import com.gyros.startchat.domain.GetCountryCodesUseCase
import com.gyros.startchat.domain.GetDefaultCountryCodeUseCase
import com.gyros.startchat.domain.GetWhatsAppUriUseCase
import com.gyros.startchat.domain.SaveChatHistoryEntryUseCase
import com.gyros.startchat.domain.SaveDefaultCountryCodeUseCase
import com.gyros.startchat.repositories.ChatHistoryRepository
import com.gyros.startchat.repositories.ChatHistoryRepositoryImpl
import com.gyros.startchat.repositories.CountryCodeRepository
import com.gyros.startchat.repositories.CountryCodeRepositoryImpl
import com.gyros.startchat.screens.history.ChatHistoryViewModel
import com.gyros.startchat.screens.startchat.StartChatViewModel
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<CountryCodesReader> { CountryCodesReaderImpl(androidContext(), "country_codes.json") }
    single<AppSettings> {
        SettingsImpl(
            settings = SharedPreferencesSettings(
                androidContext().getSharedPreferences(
                    "StartChatPrefs",
                    android.content.Context.MODE_PRIVATE
                )
            )
        )
    }
    single<ClipBoardManager> { ClipBoardManagerImpl(androidContext()) }
    single<UrlOpener> { UrlOpenerImpl(androidContext()) }
    single<AppInfo> { AppInfoImpl(androidContext()) }

    single<CountryCodeRepository> { CountryCodeRepositoryImpl(get(), get()) }
    single<ChatHistoryRepository> { ChatHistoryRepositoryImpl(get()) }

    factory { GetCountryCodesUseCase(get()) }
    factory { GetDefaultCountryCodeUseCase(get()) }
    factory { GetWhatsAppUriUseCase() }
    factory { SaveDefaultCountryCodeUseCase(get()) }
    factory { SaveChatHistoryEntryUseCase(get()) }
    factory { GetChatHistoryUseCase(get()) }

    viewModel { StartChatViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ChatHistoryViewModel(get(), get()) }
}
