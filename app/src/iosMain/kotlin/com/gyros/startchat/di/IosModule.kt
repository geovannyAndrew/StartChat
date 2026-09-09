package com.gyros.startchat.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
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
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.Clock
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask

val dataModule = module {
    single {
        val dbFilePath = documentDirectory() + "/startchat.db"
        Room.databaseBuilder<StartChatDatabase>(
            name = dbFilePath
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<StartChatDatabase>().chatHistoryDao() }
    single<CountryCodesReader> { CountryCodesReaderImpl() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
    single<AppSettings> { SettingsImpl(get()) }
    single<ClipBoardManager> { ClipBoardManagerImpl() }
    single<UrlOpener> { UrlOpenerImpl() }
    single<AppInfo> { AppInfoImpl() }
    single<PendingSharedTextStoreInterface> {
        PendingSharedTextStore(
            userDefaults = NSUserDefaults(suiteName = PendingSharedTextStoreInterface.APP_GROUP_ID)!!,
            clock = Clock.System
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    return requireNotNull(documentDirectory?.path)
}