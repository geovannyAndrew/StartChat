package com.gyros.startchat.data

import android.content.Context
import com.gyros.startchat.data.models.CountryCode
import kotlinx.serialization.json.Json

class CountryCodesReaderImpl(
    private val context: Context,
    private val assetsPath: String
) : CountryCodesReader {

    private val json = Json { ignoreUnknownKeys = true }

    private fun getJsonString(): String {
        val inputStream = context.assets.open(assetsPath)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }

    override fun read(): List<CountryCode> {
        return json.decodeFromString<List<CountryCode>>(getJsonString())
    }
}
