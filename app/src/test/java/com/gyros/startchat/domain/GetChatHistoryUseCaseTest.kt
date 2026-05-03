package com.gyros.startchat.domain

import com.gyros.startchat.repositories.ChatHistoryRepository
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetChatHistoryUseCaseTest {

    private lateinit var repository: ChatHistoryRepository
    private lateinit var sut: GetChatHistoryUseCase

    @Before
    fun setUp() {
        repository = mockk<ChatHistoryRepository>(relaxed = true)
        sut = GetChatHistoryUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke calls repository`() = runTest {
        sut.invoke()

        coVerify { repository.getHistory() }
    }
}
