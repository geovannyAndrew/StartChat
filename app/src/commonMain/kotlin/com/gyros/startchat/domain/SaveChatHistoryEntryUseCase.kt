package com.gyros.startchat.domain

import com.gyros.startchat.repositories.ChatHistoryRepository

/** Saves a phone number to the chat history with the current timestamp. */
class SaveChatHistoryEntryUseCase(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(phoneNumber: String) = repository.saveEntry(phoneNumber)
}
