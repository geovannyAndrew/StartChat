package com.gyros.startchat.domain

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWhatsAppUriUseCaseTest {
    private lateinit var sut: GetWhatsAppUriUseCase

    @Before
    fun setUp() {
        sut = GetWhatsAppUriUseCase()
    }

    @Test
    fun `invoke should return correct WhatsApp URI`() {
        val phoneNumber = "1234567890"

        val result = sut.invoke(phoneNumber)

        assertEquals("https://wa.me/$phoneNumber", result)
    }

    @Test
    fun `invoke strips leading plus sign from phone number`() {
        val result = sut.invoke("+14155552671")

        assertEquals("https://wa.me/14155552671", result)
    }
}