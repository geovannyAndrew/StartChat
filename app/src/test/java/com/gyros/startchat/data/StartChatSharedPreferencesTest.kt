package com.gyros.startchat.data

import android.content.Context
import android.content.SharedPreferences
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StartChatSharedPreferencesTest {

    private lateinit var context: Context
    private lateinit var sut: StartChatSharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    private val countryCode = "US"

    @Before
    fun setUp() {
        context = mockk<Context>(relaxed = true)
        sharedPreferences = mockk<SharedPreferences>(relaxed = true)
        every { sharedPreferences.getString(any(), null) } returns countryCode
        every { context.getSharedPreferences(any(), any()) } returns sharedPreferences
        sut = StartChatSharedPreferences(context)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun getDefaultCountryCode() {
        sut.getDefaultCountryCode()
        // Then
        verify { context.getSharedPreferences("StartChatPrefs", Context.MODE_PRIVATE) }
        verify { sharedPreferences.getString("defaultCountryCode", null) }
    }

    @Test
    fun saveDefaultCountryCode() {
        // Given
        val editor = mockk<SharedPreferences.Editor>(relaxed = true)
        every { sharedPreferences.edit() } returns editor
        val countryCodeSlot = slot<String>()
        val countryCodeKeySlot = slot<String>()
        every { editor.putString(capture(countryCodeKeySlot), capture(countryCodeSlot)) } returns editor

        // When
        sut.saveDefaultCountryCode(countryCode)

        // Then
        verify { editor.apply() }
        assertEquals(countryCode, countryCodeSlot.captured)
        assertEquals("defaultCountryCode", countryCodeKeySlot.captured)
    }
}
