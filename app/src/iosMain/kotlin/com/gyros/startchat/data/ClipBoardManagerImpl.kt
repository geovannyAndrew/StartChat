package com.gyros.startchat.data

import platform.UIKit.UIPasteboard

class ClipBoardManagerImpl : ClipBoardManager {

    override fun getPhoneNumbersFromClipBoard(): List<String> {
        val text = UIPasteboard.general.string ?: return emptyList()
        if (REGEX_VALID_PHONE_NUMBER.matches(text)) {
            return listOf(text)
        }
        return emptyList()
    }

    companion object {
        val REGEX_VALID_PHONE_NUMBER =
            Regex("""^(\+?\d{1,3}\s?)?(\(?\d{3}\)?[\s-]?)\d{3}[\s-]?\d{4}$""")
    }
}