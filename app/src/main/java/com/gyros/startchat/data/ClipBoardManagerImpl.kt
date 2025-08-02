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
        private val REGEX_VALID_PHONE_NUMBER = Regex("^[+]?[0-9]{1,3}[-\\s.]?[(]?[0-9]{1,3}[)]?[-\\s.]?[0-9]{3,4}[-\\s.]?[0-9]{4}$")
    }
}