package com.gyros.startchat.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CountryCode(
    val name: String,
    val code: String,
    val dialCode: String,
    val flag: String
)