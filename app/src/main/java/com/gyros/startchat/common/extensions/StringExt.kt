package com.gyros.startchat.common.extensions

fun String.hasCountryCode() : Boolean {
    return this.contains("+")
}


fun String.sanitizePhoneNumber() : String {
    val regex = Regex("[^0-9+]")
    return regex.replace(this, "")
}