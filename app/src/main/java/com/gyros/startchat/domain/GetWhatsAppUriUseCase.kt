package com.gyros.startchat.domain

import javax.inject.Inject

class GetWhatsAppUriUseCase @Inject constructor() {

    operator fun invoke(numberText: String): String {
        return "https://wa.me/${numberText.trim().replace("+", "")}"
    }
}