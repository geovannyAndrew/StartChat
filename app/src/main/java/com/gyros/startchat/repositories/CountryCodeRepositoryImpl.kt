package com.gyros.startchat.repositories

import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.data.StartChatSharedPreferences
import com.gyros.startchat.data.models.CountryCode

class CountryCodeRepositoryImpl(
    private val reader: CountryCodesReader,
    private val sharedPreferences: StartChatSharedPreferences
) : CountryCodeRepository {

    private val listCodes by lazy { reader.read() }

    override fun getCountryCodes(): List<CountryCode> {
        return listCodes
    }

    override fun getDefaultCountryCode(): CountryCode? {
        sharedPreferences.getDefaultCountryCode()?.let { dialCode ->
            return listCodes.firstOrNull { it.dialCode == dialCode }
        }
        return null
    }

    override fun saveDefaultCountryCode(countryCode: CountryCode?) {
        sharedPreferences.saveDefaultCountryCode(countryCode?.dialCode)
    }
}