package com.gyros.startchat.data

/** Reads phone numbers from the system clipboard. */
interface ClipBoardManager {
    fun getPhoneNumbersFromClipBoard(): List<String>
}