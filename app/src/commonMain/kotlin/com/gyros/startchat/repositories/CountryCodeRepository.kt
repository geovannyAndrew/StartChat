package com.gyros.startchat.repositories

import com.gyros.startchat.data.models.CountryCode

/** Abstraction for country code retrieval and default country code persistence. */
interface CountryCodeRepository {

    fun getCountryCodes(): List<CountryCode>

    fun getDefaultCountryCode(): CountryCode?

    fun saveDefaultCountryCode(countryCode: CountryCode?)

}