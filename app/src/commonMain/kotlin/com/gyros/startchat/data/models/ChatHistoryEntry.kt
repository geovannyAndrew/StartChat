package com.gyros.startchat.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A Room entity representing a previously dialed phone number with its timestamp. */
@Entity(tableName = "chat_history")
data class ChatHistoryEntry(
    @PrimaryKey val phoneNumber: String,
    val timestamp: Long
)
