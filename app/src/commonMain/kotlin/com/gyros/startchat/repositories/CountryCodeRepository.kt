package com.gyros.startchat.repositories

import com.gyros.startchat.data.models.CountryCode

interface CountryCodeRepository {

    fun getCountryCodes(): List<CountryCode>

    fun getDefaultCountryCode(): CountryCode?

    fun saveDefaultCountryCode(countryCode: CountryCode?)

}