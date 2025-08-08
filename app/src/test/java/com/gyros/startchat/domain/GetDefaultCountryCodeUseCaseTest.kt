package com.gyros.startchat.domain

import com.gyros.startchat.repositories.CountryCodeRepository
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetDefaultCountryCodeUseCaseTest {
    private lateinit var repository: CountryCodeRepository
    private lateinit var sut: GetDefaultCountryCodeUseCase

    @Before
    fun setUp() {
        repository = mockk<CountryCodeRepository>(relaxed = true)
        sut = GetDefaultCountryCodeUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke calls repository`() {
        sut.invoke()

        verify { repository.getDefaultCountryCode() }
    }



}