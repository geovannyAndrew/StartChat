package com.gyros.startchat.data

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class CountryCodesReaderTest {

    private val context = mockk<Context>(relaxed = true)
    private val assetsPath = "country_codes.json"

    private lateinit var sut: CountryCodesReader

    @Before
    fun setUp() {
        every { context.assets.open(assetsPath) } returns mockk(relaxed = true)
        sut = CountryCodesReaderImpl(context, assetsPath)
    }

    @Test
    fun `read with valid JSON file`() {
        val inputStreamFile = getInputStreamFromFileName("valid_json_file_with_country_codes.json")
        every { context.assets.open(assetsPath) } returns inputStreamFile

        val countryCodes = sut.read()

        assertTrue(countryCodes.isNotEmpty())
    }

    @Test
    fun `read with empty JSON array`() {
        val inputStreamFile = getInputStreamFromFileName("empty_array_json_file.json")
        every { context.assets.open(assetsPath) } returns inputStreamFile

        val countryCodes = sut.read()

        assertTrue(countryCodes.isEmpty())
    }

    @Test
    fun `read with malformed JSON`() {
        val inputStreamFile = getInputStreamFromFileName("malformed_json_file_with_country_codes.json")
        every { context.assets.open(assetsPath) } returns inputStreamFile

        assertThrows(SerializationException::class.java) {
            sut.read()
        }
    }

    @Test
    fun `read with missing asset file`() {
        every { context.assets.open(assetsPath) } throws FileNotFoundException()

        assertThrows(FileNotFoundException::class.java) {
            sut.read()
        }
    }

    @Test
    fun `read with incorrect JSON structure`() {
        val inputStreamFile = getInputStreamFromFileName("valid_json_file_with_wrong_format_country_codes.json")
        every { context.assets.open(assetsPath) } returns inputStreamFile

        assertThrows(SerializationException::class.java) {
            sut.read()
        }
    }

    @Test
    fun `read with JSON containing null values for CountryCode fields`() {
        val inputStreamFile = getInputStreamFromFileName("valid_json_file_with_null_country_codes.json")
        every { context.assets.open(assetsPath) } returns inputStreamFile

        assertThrows(SerializationException::class.java) {
            sut.read()
        }
    }

    @Test
    fun `read with JSON containing extra unexpected fields in CountryCode objects`() {
        val inputStreamFile = getInputStreamFromFileName("valid_json_file_with_extra_data_country_codes.json")
        every { context.assets.open(assetsPath) } returns inputStreamFile

        val countryCodes = sut.read()

        assertEquals(1, countryCodes.size)
    }

    private fun getInputStreamFromFileName(fileName: String): InputStream {
        val file = File("src/test/assets/${fileName}")
        return file.inputStream()
    }

}