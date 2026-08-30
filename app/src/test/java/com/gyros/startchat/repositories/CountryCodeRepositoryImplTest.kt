package com.gyros.startchat.repositories

import com.gyros.startchat.data.AppSettings
import com.gyros.startchat.data.CountryCodesReader
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
    private lateinit var settings: AppSettings
    private lateinit var sut: CountryCodeRepositoryImpl

    @Before
    fun setUp() {
        reader = mockk(relaxed = true)
        settings = mockk(relaxed = true)
        sut = CountryCodeRepositoryImpl(reader, settings)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getCountryCodes returns list from reader`() {
        val expected = listOf(mockCountryCode())
        every { reader.read() } returns expected

        val actual = sut.getCountryCodes()

        assertEquals(actual, expected)
        verify { reader.read() }
    }

    @Test
    fun `getDefaultCountryCode returns code from settings`() {
        val expected = "+1"
        val listCountryCodeExpected = listOf(mockCountryCode())
        every { reader.read() } returns listCountryCodeExpected
        every { settings.getDefaultCountryCode() } returns expected

        val actual = sut.getDefaultCountryCode()

        assertEquals(listCountryCodeExpected.firstOrNull(), actual)
        verify { settings.getDefaultCountryCode() }
    }

    @Test
    fun `getDefaultCountryCode returns null from settings`() {
        val listCountryCodeExpected = listOf(mockCountryCode())
        every { reader.read() } returns listCountryCodeExpected
        every { settings.getDefaultCountryCode() } returns null

        val actual = sut.getDefaultCountryCode()

        assertNull(actual)
        verify { settings.getDefaultCountryCode() }
    }

    @Test
    fun `saveDefaultCountryCode calls settings`() {
        val countryCode = mockCountryCode()

        sut.saveDefaultCountryCode(countryCode)

        verify { settings.saveDefaultCountryCode(countryCode.dialCode) }
    }

}