package com.gyros.startchat.screens.startchat

import android.net.Uri
import com.gyros.startchat.data.ClipBoardManager
import com.gyros.startchat.data.models.CountryCode
import com.gyros.startchat.domain.GetCountryCodesUseCase
import com.gyros.startchat.domain.GetDefaultCountryCodeUseCase
import com.gyros.startchat.domain.GetWhatsAppUriUseCase
import com.gyros.startchat.domain.SaveChatHistoryEntryUseCase
import com.gyros.startchat.domain.SaveDefaultCountryCodeUseCase
import com.gyros.startchat.mockCountryCode
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StartChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val saveDefaultCountryCodeUseCase = mockk<SaveDefaultCountryCodeUseCase>(relaxed = true)
    private val getCountryCodesUseCase = mockk<GetCountryCodesUseCase>(relaxed = true)
    private val getDefaultCountryCodeUseCase = mockk<GetDefaultCountryCodeUseCase>(relaxed = true)
    private val getWhatsAppUriUseCase = mockk<GetWhatsAppUriUseCase>(relaxed = true)
    private val clipBoardManager = mockk<ClipBoardManager>(relaxed = true)
    private val saveHistoryEntryUseCase = mockk<SaveChatHistoryEntryUseCase>(relaxed = true)

    private lateinit var sut: StartChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getCountryCodesUseCase() } returns listOf(mockCountryCode())
        every { getDefaultCountryCodeUseCase() } returns mockCountryCode()
        every { clipBoardManager.getPhoneNumbersFromClipBoard() } returns emptyList()
        sut = StartChatViewModel(
            saveDefaultCountryCodeUseCase,
            getCountryCodesUseCase,
            getDefaultCountryCodeUseCase,
            getWhatsAppUriUseCase,
            clipBoardManager,
            saveHistoryEntryUseCase
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    // region start()

    @Test
    fun `start with null loads country codes into state`() {
        sut.start(null)

        val state = sut.state.value
        assertEquals(listOf(mockCountryCode()), state.countryCodes)
        assertEquals(mockCountryCode(), state.selectedCountryCode)
    }

    @Test
    fun `start with actionText that has country code emits StartIntentAction`() = runTest {
        val uri = mockk<Uri>()
        every { getWhatsAppUriUseCase(any()) } returns uri

        var capturedEvent: StartChatViewModel.Events? = null
        val job = launch { sut.events.collect { capturedEvent = it } }

        sut.start("+14155552671")
        advanceUntilIdle()

        assert(capturedEvent is StartChatViewModel.Events.StartIntentAction)
        job.cancel()
    }

    @Test
    fun `start with actionText that has country code saves history entry`() = runTest {
        val uri = mockk<Uri>()
        every { getWhatsAppUriUseCase(any()) } returns uri

        sut.start("+14155552671")
        advanceUntilIdle()

        coVerify { saveHistoryEntryUseCase("+14155552671") }
    }

    @Test
    fun `start with actionText without country code updates state with phone number`() = runTest {
        sut.start("3212345678")
        advanceUntilIdle()

        val state = sut.state.value
        assertEquals("3212345678", state.phoneNumber)
        assertNotNull(state.countryCodes)
    }

    // endregion

    // region cleanText() via onEditTextChange

    @Test
    fun `editing text with valid phone enables start chat button`() {
        sut.start(null)

        sut.state.value.onEditTextChange("+14155552671")

        assertNotNull(sut.state.value.onStartChat)
    }

    @Test
    fun `editing text with invalid phone disables start chat button`() {
        sut.start(null)

        sut.state.value.onEditTextChange("123")

        assertNull(sut.state.value.onStartChat)
    }

    @Test
    fun `editing text with country code hides country code selector`() {
        sut.start(null)

        sut.state.value.onEditTextChange("+14155552671")

        assertNull(sut.state.value.countryCodes)
        assertNull(sut.state.value.selectedCountryCode)
    }

    @Test
    fun `editing text without country code keeps country code selector visible`() {
        sut.start(null)

        sut.state.value.onEditTextChange("3212345678")

        assertNotNull(sut.state.value.countryCodes)
    }

    // endregion

    // region selectCountryCode() via onCountryCodeSelected

    @Test
    fun `selecting country code saves it and updates state`() {
        val newCountryCode = CountryCode(code = "CO", name = "Colombia", dialCode = "+57", flag = "CO")

        sut.state.value.onCountryCodeSelected(newCountryCode)

        verify { saveDefaultCountryCodeUseCase(newCountryCode) }
        assertEquals(newCountryCode, sut.state.value.selectedCountryCode)
    }

    // endregion

    // region onResume()

    @Test
    fun `onResume updates clipboard numbers`() = runTest {
        val numbers = listOf("+14155552671", "3212345678")
        every { clipBoardManager.getPhoneNumbersFromClipBoard() } returns numbers

        sut.onResume()
        advanceUntilIdle()

        assertEquals(numbers, sut.state.value.numbersOnClipBoard)
    }

    @Test
    fun `onResume filters out the current phone number from clipboard`() = runTest {
        val currentPhone = "3212345678"
        every { clipBoardManager.getPhoneNumbersFromClipBoard() } returns listOf(currentPhone, "+14155552671")
        sut.state.value.onEditTextChange(currentPhone)

        sut.onResume()
        advanceUntilIdle()

        assertEquals(listOf("+14155552671"), sut.state.value.numbersOnClipBoard)
    }

    @Test
    fun `onResume with empty clipboard sets empty list`() = runTest {
        every { clipBoardManager.getPhoneNumbersFromClipBoard() } returns emptyList()

        sut.onResume()
        advanceUntilIdle()

        assertEquals(emptyList<String>(), sut.state.value.numbersOnClipBoard)
    }

    // endregion
}
