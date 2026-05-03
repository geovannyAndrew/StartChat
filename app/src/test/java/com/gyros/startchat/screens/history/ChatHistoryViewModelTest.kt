package com.gyros.startchat.screens.history

import android.net.Uri
import com.gyros.startchat.data.models.ChatHistoryEntry
import com.gyros.startchat.domain.GetChatHistoryUseCase
import com.gyros.startchat.domain.GetWhatsAppUriUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatHistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getChatHistoryUseCase = mockk<GetChatHistoryUseCase>(relaxed = true)
    private val getWhatsAppUriUseCase = mockk<GetWhatsAppUriUseCase>(relaxed = true)

    private lateinit var sut: ChatHistoryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sut = ChatHistoryViewModel(getChatHistoryUseCase, getWhatsAppUriUseCase)
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    // region load()

    @Test
    fun `load populates entries from use case`() = runTest {
        val entries = listOf(
            ChatHistoryEntry("+14155552671", 2000L),
            ChatHistoryEntry("+573212345678", 1000L)
        )
        coEvery { getChatHistoryUseCase() } returns entries

        sut.load()
        advanceUntilIdle()

        assertEquals(entries, sut.state.value.entries)
    }

    @Test
    fun `load with empty list sets entries to empty`() = runTest {
        coEvery { getChatHistoryUseCase() } returns emptyList()

        sut.load()
        advanceUntilIdle()

        assertTrue(sut.state.value.entries.isEmpty())
    }

    // endregion

    // region onEntryClicked

    @Test
    fun `onEntryClicked calls getWhatsAppUriUseCase with entry phoneNumber`() = runTest {
        val entry = ChatHistoryEntry("+14155552671", 1000L)
        val uri = mockk<Uri>()
        every { getWhatsAppUriUseCase(any()) } returns uri

        sut.state.value.onEntryClicked?.invoke(entry)
        advanceUntilIdle()

        coVerify { getWhatsAppUriUseCase(entry.phoneNumber) }
    }

    @Test
    fun `onEntryClicked emits OpenWhatsApp event with correct uri`() = runTest {
        val entry = ChatHistoryEntry("+14155552671", 1000L)
        val uri = mockk<Uri>()
        every { getWhatsAppUriUseCase(any()) } returns uri

        var capturedEvent: ChatHistoryViewModel.Events? = null
        val job = launch { sut.events.collect { capturedEvent = it } }

        sut.state.value.onEntryClicked?.invoke(entry)
        advanceUntilIdle()

        assert(capturedEvent is ChatHistoryViewModel.Events.OpenWhatsApp) {
            "Expected OpenWhatsApp event but got $capturedEvent"
        }
        assertEquals(uri, (capturedEvent as ChatHistoryViewModel.Events.OpenWhatsApp).uri)
        job.cancel()
    }

    // endregion
}
