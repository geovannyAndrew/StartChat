package com.gyros.startchat.di

import androidx.room.Room
import com.gyros.startchat.data.AppSettings
import com.gyros.startchat.data.AppInfo
import com.gyros.startchat.data.ClipBoardManager
import com.gyros.startchat.data.ClipBoardManagerImpl
import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.data.CountryCodesReaderImpl
import com.gyros.startchat.data.StartChatDatabase
import com.russhwolf.settings.Settings
import com.russhwolf.settings.MemorySettings
import org.koin.dsl.module
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

val databaseModule = module {
    single {
        Room.databaseBuilder(
            StartChatDatabase::class.java,
            "start_chat_db",
            BundledSQLiteDriver()
        ).build()
    }
    single { get<StartChatDatabase>().chatHistoryDao() }
    single<CountryCodesReader> { CountryCodesReaderImpl() }
    single<Settings> { MemorySettings() }
    single<AppSettings> { SettingsImpl(get()) }
    single<ClipBoardManager> { ClipBoardManagerImpl() }
}