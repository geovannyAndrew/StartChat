package com.gyros.startchat.data

/** Represents text received via the platform share extension, with the time it was shared. */
data class PendingSharedText(
    val text: String,
    val timestampEpochMs: Long
)
