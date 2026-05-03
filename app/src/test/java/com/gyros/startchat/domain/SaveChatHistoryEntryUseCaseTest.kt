package com.gyros.startchat.domain

import com.gyros.startchat.repositories.ChatHistoryRepository
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class SaveChatHistoryEntryUseCaseTest {

    private lateinit var repository: ChatHistoryRepository
    private lateinit var sut: SaveChatHistoryEntryUseCase

    @Before
    fun setUp() {
        repository = mockk<ChatHistoryRepository>(relaxed = true)
        sut = SaveChatHistoryEntryUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke calls repository with phoneNumber`() = runTest {
        sut.invoke("+14155552671")

        coVerify { repository.saveEntry("+14155552671") }
    }
}
