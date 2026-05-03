package com.gyros.startchat.repositories

import com.gyros.startchat.data.models.ChatHistoryEntry

interface ChatHistoryRepository {
    suspend fun getHistory(): List<ChatHistoryEntry>
    suspend fun saveEntry(phoneNumber: String)
}
