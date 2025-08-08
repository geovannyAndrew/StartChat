package com.gyros.startchat.domain

import com.gyros.startchat.mockCountryCode
import com.gyros.startchat.repositories.CountryCodeRepository
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class SaveDefaultCountryCodeUseCaseTest {

    private lateinit var repository: CountryCodeRepository
    private lateinit var sut: SaveDefaultCountryCodeUseCase

    @Before
    fun setUp() {
        repository = mockk<CountryCodeRepository>(relaxed = true)
        sut = SaveDefaultCountryCodeUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke calls repository`() {
        val countryCode = mockCountryCode()
        sut.invoke(countryCode)

        verify { repository.saveDefaultCountryCode(countryCode) }
    }

}