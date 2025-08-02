package com.gyros.startchat.common.extensions

fun String.hasCountryCode() : Boolean {
    return this.contains("+")
}


fun String.sanitizePhoneNumber() : String {
    val regex = Regex("[^0-9+]")
    return regex.replace(this, "")
}

fun String.isValidBasicPhone(): Boolean {
    val basicPhoneNumberRegex = Regex("^[+]?[0-9]{1,3}[-\\s.]?[(]?[0-9]{1,3}[)]?[-\\s.]?[0-9]{3,4}[-\\s.]?[0-9]{4}$")
    return basicPhoneNumberRegex.matches(this.trim())
}