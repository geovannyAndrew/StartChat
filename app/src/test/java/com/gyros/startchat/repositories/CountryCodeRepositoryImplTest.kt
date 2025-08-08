package com.gyros.startchat.repositories

import com.gyros.startchat.data.CountryCodesReader
import com.gyros.startchat.data.StartChatSharedPreferences
import com.gyros.startchat.mockCountryCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CountryCodeRepositoryImplTest {

    private lateinit var reader: CountryCodesReader
    private lateinit var sharedPreferences: StartChatSharedPreferences
    private lateinit var sut: CountryCodeRepositoryImpl

    @Before
    fun setUp() {
        reader = mockk(relaxed = true)
        sharedPreferences = mockk(relaxed = true)
        sut = CountryCodeRepositoryImpl(reader, sharedPreferences)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getCountryCodes returns list from reader`() {
        val expected = listOf(mockCountryCode())
        every { reader.getCountryCodes() } returns expected

        val actual = sut.getCountryCodes()

        assertEquals(actual, expected)
        verify { reader.getCountryCodes() }
    }

    @Test
    fun `getDefaultCountryCode returns code from shared preferences`() {
        val expected = "+1"
        val listCountryCodeExpected = listOf(mockCountryCode())
        every { reader.getCountryCodes() } returns listCountryCodeExpected
        every { sharedPreferences.getDefaultCountryCode() } returns expected

        val actual = sut.getDefaultCountryCode()

        assertEquals(listCountryCodeExpected.firstOrNull(), actual)
        verify { sharedPreferences.getDefaultCountryCode() }
    }

    @Test
    fun `getDefaultCountryCode returns null from shared preferences`() {
        val listCountryCodeExpected = listOf(mockCountryCode())
        every { reader.getCountryCodes() } returns listCountryCodeExpected
        every { sharedPreferences.getDefaultCountryCode() } returns null

        val actual = sut.getDefaultCountryCode()

        assertNull(actual)
        verify { sharedPreferences.getDefaultCountryCode() }
    }

    @Test
    fun `saveDefaultCountryCode calls shared preferences`() {
        val countryCode = mockCountryCode()

        sut.saveDefaultCountryCode(countryCode)

        verify { sharedPreferences.saveDefaultCountryCode(countryCode.dialCode) }
    }

}