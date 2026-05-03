package com.gyros.startchat.domain

import com.gyros.startchat.repositories.ChatHistoryRepository
import javax.inject.Inject

class SaveChatHistoryEntryUseCase @Inject constructor(
    private val repository: ChatHistoryRepository
) {
    suspend operator fun invoke(phoneNumber: String) = repository.saveEntry(phoneNumber)
}
