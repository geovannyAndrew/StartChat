package com.gyros.startchat.data

interface AppSettings {
    fun saveDefaultCountryCode(dialCode: String?)
    fun getDefaultCountryCode(): String?
}
