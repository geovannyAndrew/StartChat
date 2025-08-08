package com.gyros.startchat

import com.gyros.startchat.data.models.CountryCode

const val PHONE_NUMBER_1 = "3212345678" // Assuming this is a mobile number based on context
const val PHONE_NUMBER_2 = "+14155552671" // Common US mobile format with country code
const val PHONE_NUMBER_3 = "07700900000"  // Common UK mobile format
const val PHONE_NUMBER_4 = "+4917612345678" // Common German mobile format with country code
const val PHONE_NUMBER_5 = "9876543210" // Generic 10-digit mobile number

const val INVALID_PHONE_NUMBER_1 = "123" // Too short
const val INVALID_PHONE_NUMBER_2 = "abcdefghij" // Contains letters
const val INVALID_PHONE_NUMBER_3 = "+12345678901234567890" // Too long
const val INVALID_PHONE_NUMBER_4 = "555-5555" // Missing area code / invalid format
const val INVALID_PHONE_NUMBER_5 = "" // Empty string

fun mockCountryCode(): CountryCode {
    return CountryCode(
        code = "US",
        name = "United States",
        dialCode = "+1",
        flag = "US"
    )
}
