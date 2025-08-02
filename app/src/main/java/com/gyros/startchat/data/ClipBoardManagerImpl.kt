package com.gyros.startchat.data

import android.content.Context
import com.gyros.startchat.common.extensions.getFromClipBoard
import javax.inject.Inject

class ClipBoardManagerImpl @Inject constructor(val context: Context) : ClipBoardManager {

    override fun getPhoneNumbersFromClipBoard(): List<String> {
        val listPhonesFromClipBoard = context.getFromClipBoard(
            maxItems = 3,
            regex = REGEX_VALID_PHONE_NUMBER
        )
        return listPhonesFromClipBoard
    }

    companion object {
        private val REGEX_VALID_PHONE_NUMBER = Regex("""^(\+?\d{1,3}\s?)?(\(?\d{3}\)?[\s-]?)\d{3}[\s-]?\d{4}$""")
    }
}