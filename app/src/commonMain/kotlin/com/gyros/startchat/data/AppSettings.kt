package com.gyros.startchat.data

/** Abstraction for key-value app settings (e.g., default country code). */
interface AppSettings {
    fun saveDefaultCountryCode(dialCode: String?)
    fun getDefaultCountryCode(): String?
}
