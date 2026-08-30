package com.gyros.startchat.repositories

import com.gyros.startchat.data.ChatHistoryDao
import com.gyros.startchat.data.models.ChatHistoryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ChatHistoryRepositoryImpl(
    private val dao: ChatHistoryDao
) : ChatHistoryRepository {

    override suspend fun getHistory(): List<ChatHistoryEntry> = withContext(Dispatchers.Default) {
        dao.getAll()
    }

    override suspend fun saveEntry(phoneNumber: String) = withContext(Dispatchers.Default) {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        dao.upsert(ChatHistoryEntry(phoneNumber, timestamp))
    }
}
