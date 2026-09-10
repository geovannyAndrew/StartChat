package com.gyros.startchat.di

import androidx.room.Room
import com.gyros.startchat.data.AppInfo
import com.gyros.startchat.data.AppInfoImpl
import com.gyros.startchat.data.AppSettings
import com.gyros.startchat.data.ClipBoardManager
import com.gyros.startchat.data.ClipBoardManagerImpl
import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.data.CountryCodesReaderImpl
import com.gyros.startchat.data.PendingSharedTextStore
import com.gyros.startchat.data.PendingSharedTextStoreInterface
import com.gyros.startchat.data.SettingsImpl
import com.gyros.startchat.data.StartChatDatabase
import com.gyros.startchat.data.UrlOpener
import com.gyros.startchat.data.UrlOpenerImpl
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings.Factory
import kotlinx.datetime.Clock
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Android-specific Koin module providing Room DB, platform services, and settings. */
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
    single<UrlOpener> { UrlOpenerImpl(androidContext()) }
    single<AppInfo> { AppInfoImpl(androidContext()) }
    single<PendingSharedTextStoreInterface> {
        val context = androidContext()
        val prefs = context.getSharedPreferences(
            "pending_shared_text",
            android.content.Context.MODE_PRIVATE
        )
        PendingSharedTextStore(prefs, Clock.System)
    }
}
