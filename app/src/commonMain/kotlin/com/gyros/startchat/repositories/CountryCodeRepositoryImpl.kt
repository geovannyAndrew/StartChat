package com.gyros.startchat.repositories

import com.gyros.startchat.data.AppSettings
import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.data.models.CountryCode

/** Reads country codes from [CountryCodesReader] and persists the default via [AppSettings]. */
class CountryCodeRepositoryImpl(
    private val reader: CountryCodesReader,
    private val settings: AppSettings
) : CountryCodeRepository {

    private val listCodes by lazy { reader.read() }

    override fun getCountryCodes(): List<CountryCode> {
        return listCodes
    }

    override fun getDefaultCountryCode(): CountryCode? {
        settings.getDefaultCountryCode()?.let { dialCode ->
            return listCodes.firstOrNull { it.dialCode == dialCode }
        }
        return null
    }

    override fun saveDefaultCountryCode(countryCode: CountryCode?) {
        settings.saveDefaultCountryCode(countryCode?.dialCode)
    }
}