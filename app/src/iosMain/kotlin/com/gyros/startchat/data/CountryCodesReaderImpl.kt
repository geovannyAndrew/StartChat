package com.gyros.startchat.data

import com.gyros.startchat.data.models.CountryCode
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create

class CountryCodesReaderImpl : CountryCodesReader {

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun getJsonString(): String {
        val bundle = NSBundle.mainBundle
        val path = bundle.pathForResource("country_codes", "json")
            ?: return "[]"
        return try {
            val data = NSFileManager.defaultManager.contentsAtPath(path)
            data?.let { NSString.create(data = it, encoding = NSUTF8StringEncoding) as String }
                ?: "[]"
        } catch (e: Exception) {
            "[]"
        }
    }

    override fun read(): List<CountryCode> {
        return json.decodeFromString<List<CountryCode>>(getJsonString())
    }
}
