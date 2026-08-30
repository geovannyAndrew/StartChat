package com.gyros.startchat.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.gyros.startchat.data.AppSettings
import com.gyros.startchat.data.ClipBoardManager
import com.gyros.startchat.data.ClipBoardManagerImpl
import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.data.CountryCodesReaderImpl
import com.gyros.startchat.data.SettingsImpl
import com.gyros.startchat.data.StartChatDatabase
import com.russhwolf.settings.Settings
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
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
    single<Settings> { MapSettings() }
    single<AppSettings> { SettingsImpl(get()) }
    single<ClipBoardManager> { ClipBoardManagerImpl() }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(documentDirectory?.path)
}

class MapSettings : Settings {
    private val map = mutableMapOf<String, String>()

    override fun getString(key: String, defaultValue: String): String = map[key] ?: defaultValue
    override fun putString(key: String, value: String) {
        map[key] = value
    }

    override fun getStringOrNull(key: String): String? = map[key]
    override fun remove(key: String) {
        map.remove(key)
    }

    override fun clear() {
        map.clear()
    }

    override fun hasKey(key: String): Boolean = map.containsKey(key)
    override val size: Int get() = map.size

    override fun getInt(key: String, defaultValue: Int): Int =
        map[key]?.toIntOrNull() ?: defaultValue

    override fun putInt(key: String, value: Int) {
        map[key] = value.toString()
    }

    override fun getIntOrNull(key: String): Int? = map[key]?.toIntOrNull()
    override fun getLong(key: String, defaultValue: Long): Long =
        map[key]?.toLongOrNull() ?: defaultValue

    override fun putLong(key: String, value: Long) {
        map[key] = value.toString()
    }

    override fun getLongOrNull(key: String): Long? = map[key]?.toLongOrNull()
    override fun getFloat(key: String, defaultValue: Float): Float =
        map[key]?.toFloatOrNull() ?: defaultValue

    override fun putFloat(key: String, value: Float) {
        map[key] = value.toString()
    }

    override fun getFloatOrNull(key: String): Float? = map[key]?.toFloatOrNull()
    override fun getDouble(key: String, defaultValue: Double): Double =
        map[key]?.toDoubleOrNull() ?: defaultValue

    override fun putDouble(key: String, value: Double) {
        map[key] = value.toString()
    }

    override fun getDoubleOrNull(key: String): Double? = map[key]?.toDoubleOrNull()
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        map[key]?.toBooleanStrictOrNull() ?: defaultValue

    override fun putBoolean(key: String, value: Boolean) {
        map[key] = value.toString()
    }

    override fun getBooleanOrNull(key: String): Boolean? = map[key]?.toBooleanStrictOrNull()
    override val keys: Set<String> get() = map.keys.toSet()
}