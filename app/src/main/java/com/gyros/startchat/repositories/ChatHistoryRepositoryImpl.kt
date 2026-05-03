package com.gyros.startchat.repositories

import com.gyros.startchat.data.ChatHistoryDao
import com.gyros.startchat.data.models.ChatHistoryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatHistoryRepositoryImpl @Inject constructor(
    private val dao: ChatHistoryDao
) : ChatHistoryRepository {

    override suspend fun getHistory(): List<ChatHistoryEntry> = withContext(Dispatchers.IO) {
        dao.getAll()
    }

    override suspend fun saveEntry(phoneNumber: String) = withContext(Dispatchers.IO) {
        dao.upsert(ChatHistoryEntry(phoneNumber, System.currentTimeMillis()))
    }
}
