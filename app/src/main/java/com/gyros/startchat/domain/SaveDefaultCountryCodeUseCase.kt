package com.gyros.startchat.domain

import com.gyros.startchat.data.models.CountryCode
import com.gyros.startchat.repositories.CountryCodeRepository

class SaveDefaultCountryCodeUseCase(
    private val repository: CountryCodeRepository
) {
    operator fun invoke(countryCode: CountryCode?) {
        repository.saveDefaultCountryCode(countryCode)
    }
}