package com.gyros.startchat.domain

import com.gyros.startchat.data.models.CountryCode
import com.gyros.startchat.repositories.CountryCodeRepository
import javax.inject.Inject

class GetDefaultCountryCodeUseCase @Inject constructor(
    val repository: CountryCodeRepository
) {

    operator fun invoke(): CountryCode? {
        return repository.getDefaultCountryCode()
    }
}