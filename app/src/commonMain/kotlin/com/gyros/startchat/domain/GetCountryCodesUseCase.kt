package com.gyros.startchat.domain

import com.gyros.startchat.data.models.CountryCode
import com.gyros.startchat.repositories.CountryCodeRepository

/** Returns the full list of supported country codes. */
class GetCountryCodesUseCase(
    private val repository: CountryCodeRepository
) {

    operator fun invoke(): List<CountryCode> {
        return repository.getCountryCodes()
    }
}