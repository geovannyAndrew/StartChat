package com.gyros.startchat.data

import com.gyros.startchat.data.models.CountryCode
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.InternalComposeResourceApi
import org.jetbrains.compose.resources.readResourceBytes

class CountryCodesReaderImpl : CountryCodesReader {

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(InternalComposeResourceApi::class)
    private fun getJsonString(): String {
        val bytes = readResourceBytes("files", "country_codes.json")
        return bytes.decodeToString()
    }

    override fun read(): List<CountryCode> {
        return json.decodeFromString<List<CountryCode>>(getJsonString())
    }
}