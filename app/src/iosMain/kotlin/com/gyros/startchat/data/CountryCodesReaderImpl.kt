package com.gyros.startchat.data

import com.gyros.startchat.data.models.CountryCode
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import platform.Foundation.NSBundle

class CountryCodesReaderImpl : CountryCodesReader {

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalForeignApi::class)
    private fun getJsonString(): String {
        val bundle = NSBundle.mainBundle
        return bundle.pathForResource("country_codes", "json")?.let { path ->
            try {
                platform.Foundation.NSFileManager.defaultManager.contentsAtPath(path)?.let { data ->
                    data.toString()
                } ?: "[]"
            } catch (e: Exception) {
                "[]"
            }
        } ?: "[]"
    }

    override fun read(): List<CountryCode> {
        return json.decodeFromString<List<CountryCode>>(getJsonString())
    }
}