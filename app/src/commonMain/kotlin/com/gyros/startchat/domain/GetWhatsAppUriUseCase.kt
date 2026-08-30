package com.gyros.startchat.domain

class GetWhatsAppUriUseCase {

    operator fun invoke(numberText: String): String {
        return "https://wa.me/${numberText.trim().replace("+", "")}"
    }
}