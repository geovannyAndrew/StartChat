package com.gyros.startchat.data

import com.gyros.startchat.data.models.CountryCode

/** Reads country codes from a platform-specific JSON asset. */
interface CountryCodesReader {
    fun read(): List<CountryCode>
}
