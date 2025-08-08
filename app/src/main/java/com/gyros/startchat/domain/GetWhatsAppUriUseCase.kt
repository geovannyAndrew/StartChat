package com.gyros.startchat.domain

import android.net.Uri
import androidx.core.net.toUri
import javax.inject.Inject

class GetWhatsAppUriUseCase @Inject constructor() {

    operator fun invoke(numberText: String): Uri {
        val uri = "https://wa.me/${numberText.trim().replace("+", "")}".toUri()
        return uri
    }
}