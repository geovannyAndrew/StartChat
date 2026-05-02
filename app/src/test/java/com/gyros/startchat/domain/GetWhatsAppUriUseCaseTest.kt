package com.gyros.startchat.domain

import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetWhatsAppUriUseCaseTest {
    private lateinit var sut: GetWhatsAppUriUseCase

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
        sut = GetWhatsAppUriUseCase()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke should return correct WhatsApp URI`() {
        val phoneNumber = "1234567890"

        sut.invoke(phoneNumber)

        verify { Uri.parse("https://wa.me/$phoneNumber") }
    }

    @Test
    fun `invoke strips leading plus sign from phone number`() {
        sut.invoke("+14155552671")

        verify { Uri.parse("https://wa.me/14155552671") }
    }
}