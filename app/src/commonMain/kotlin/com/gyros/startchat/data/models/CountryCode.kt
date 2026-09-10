package com.gyros.startchat.data.models

import kotlinx.serialization.Serializable

/** Represents a country with its name, ISO code, dial code (including `+` prefix), and flag emoji. */
@Serializable
data class CountryCode(
    val name: String,
    val code: String,
    val dialCode: String,
    val flag: String
)