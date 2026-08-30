package com.gyros.startchat.di

import androidx.room.Room
import com.gyros.startchat.data.AppSettings
import com.gyros.startchat.data.ClipBoardManager
import com.gyros.startchat.data.ClipBoardManagerImpl
import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.data.CountryCodesReaderImpl
import com.gyros.startchat.data.SettingsImpl
import com.gyros.startchat.data.StartChatDatabase
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings.Factory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), StartChatDatabase::class.java, "start_chat_db")
            .build()
    }
    single { get<StartChatDatabase>().chatHistoryDao() }
    single<CountryCodesReader> { CountryCodesReaderImpl(androidContext(), "country_codes.json") }
    single<Settings> {
        val factory = Factory(context = androidContext())
        factory.create("app_settings")
    }
    single<AppSettings> { SettingsImpl(get()) }
    single<ClipBoardManager> { ClipBoardManagerImpl(androidContext()) }
}
