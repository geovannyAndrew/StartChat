package com.gyros.startchat.domain

/** Builds a `wa.me` deep-link URI from a phone number string. */
class GetWhatsAppUriUseCase {

    operator fun invoke(numberText: String): String {
        return "https://wa.me/${numberText.trim().replace("+", "")}"
    }
}