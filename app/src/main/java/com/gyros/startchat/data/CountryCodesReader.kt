package com.gyros.startchat.data

import com.gyros.startchat.data.models.CountryCode

interface CountryCodesReader {
    fun read(): List<CountryCode>
}
