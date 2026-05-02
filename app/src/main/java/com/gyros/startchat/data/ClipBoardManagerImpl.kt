package com.gyros.startchat.data

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.gyros.startchat.common.extensions.getFromClipBoard
import javax.inject.Inject

class ClipBoardManagerImpl @Inject constructor(private val context: Context) : ClipBoardManager {

    override fun getPhoneNumbersFromClipBoard(): List<String> {
        return context.getFromClipBoard(maxItems = 3, regex = REGEX_VALID_PHONE_NUMBER)
    }

    companion object {

        @VisibleForTesting
        val REGEX_VALID_PHONE_NUMBER = Regex("""^(\+?\d{1,3}\s?)?(\(?\d{3}\)?[\s-]?)\d{3}[\s-]?\d{4}$""")
    }
}