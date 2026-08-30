package com.gyros.startchat.domain

import com.gyros.startchat.repositories.ChatHistoryRepository

class SaveChatHistoryEntryUseCase(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(phoneNumber: String) = repository.saveEntry(phoneNumber)
}
