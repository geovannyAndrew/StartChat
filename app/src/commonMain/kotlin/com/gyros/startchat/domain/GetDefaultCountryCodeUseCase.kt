package com.gyros.startchat.domain

import com.gyros.startchat.data.models.CountryCode
import com.gyros.startchat.repositories.CountryCodeRepository

/** Retrieves the user's saved default country code, or `null` if none is set. */
class GetDefaultCountryCodeUseCase(
    private val repository: CountryCodeRepository
) {

    operator fun invoke(): CountryCode? {
        return repository.getDefaultCountryCode()
    }
}