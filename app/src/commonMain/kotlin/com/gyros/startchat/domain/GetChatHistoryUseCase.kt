package com.gyros.startchat.domain

import com.gyros.startchat.data.models.ChatHistoryEntry
import com.gyros.startchat.repositories.ChatHistoryRepository

/** Retrieves the list of recent chat history entries, ordered by most recent. */
class GetChatHistoryUseCase(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(): List<ChatHistoryEntry> = repository.getHistory()
}
