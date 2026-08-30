package com.gyros.startchat.data

import com.russhwolf.settings.Settings

class SettingsImpl(
    private val settings: Settings
) : AppSettings {

    override fun saveDefaultCountryCode(dialCode: String?) {
        if (dialCode != null) {
            settings.putString(DEFAULT_COUNTRY_CODE, dialCode)
        } else {
            settings.remove(DEFAULT_COUNTRY_CODE)
        }
    }

    override fun getDefaultCountryCode(): String? {
        return settings.getStringOrNull(DEFAULT_COUNTRY_CODE)
    }

    companion object {
        private const val DEFAULT_COUNTRY_CODE = "defaultCountryCode"
    }
}