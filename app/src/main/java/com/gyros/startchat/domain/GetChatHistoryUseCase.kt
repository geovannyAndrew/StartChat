package com.gyros.startchat.domain

import com.gyros.startchat.data.models.ChatHistoryEntry
import com.gyros.startchat.repositories.ChatHistoryRepository
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(): List<ChatHistoryEntry> = repository.getHistory()
}
