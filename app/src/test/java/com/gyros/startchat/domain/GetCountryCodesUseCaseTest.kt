package com.gyros.startchat.domain

import com.gyros.startchat.repositories.CountryCodeRepository
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class GetCountryCodesUseCaseTest {
    private lateinit var repository: CountryCodeRepository
    private lateinit var sut: GetCountryCodesUseCase

    @Before
    fun setUp() {
        repository = mockk<CountryCodeRepository>(relaxed = true)
        sut = GetCountryCodesUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke calls repository`() {
        sut.invoke()

        verify { repository.getCountryCodes() }
    }

}