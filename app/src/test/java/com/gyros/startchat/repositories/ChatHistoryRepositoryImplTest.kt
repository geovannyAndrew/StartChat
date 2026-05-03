package com.gyros.startchat.repositories

import com.gyros.startchat.data.ChatHistoryDao
import com.gyros.startchat.data.models.ChatHistoryEntry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChatHistoryRepositoryImplTest {

    private lateinit var dao: ChatHistoryDao
    private lateinit var sut: ChatHistoryRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk<ChatHistoryDao>(relaxed = true)
        sut = ChatHistoryRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getHistory delegates to dao getAll`() = runTest {
        val entries = listOf(ChatHistoryEntry("+14155552671", 1000L))
        coEvery { dao.getAll() } returns entries

        val result = sut.getHistory()

        assertEquals(entries, result)
        coVerify { dao.getAll() }
    }

    @Test
    fun `saveEntry upserts entry with correct phoneNumber`() = runTest {
        val slot = slot<ChatHistoryEntry>()
        coEvery { dao.upsert(capture(slot)) } returns Unit

        sut.saveEntry("+14155552671")

        coVerify { dao.upsert(any()) }
        assertEquals("+14155552671", slot.captured.phoneNumber)
    }

    @Test
    fun `saveEntry timestamp is close to current time`() = runTest {
        val before = System.currentTimeMillis()
        val slot = slot<ChatHistoryEntry>()
        coEvery { dao.upsert(capture(slot)) } returns Unit

        sut.saveEntry("+14155552671")

        val after = System.currentTimeMillis()
        assert(slot.captured.timestamp in before..after) {
            "Timestamp ${slot.captured.timestamp} is outside expected range [$before, $after]"
        }
    }
}
