package com.gyros.startchat.data

import android.content.Context
import com.gyros.startchat.common.extensions.getFromClipBoard
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class ClipBoardManagerImplTest {

    private val context = mockk<Context>(relaxed = true)
    private lateinit var sut: ClipBoardManagerImpl

    @Before
    fun setUp() {
        mockkStatic(Context::getFromClipBoard)
        sut = ClipBoardManagerImpl(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getPhoneNumbersFromClipBoard passes right maxItems and regex`() {
        val slotMaxItems = slot<Int>()
        val slotRegex = slot<Regex>()

        every {
            context.getFromClipBoard(maxItems = capture(slotMaxItems), regex = capture(slotRegex))
        } returns emptyList()

        sut.getPhoneNumbersFromClipBoard()

        assertEquals(3, slotMaxItems.captured)
        assertEquals(ClipBoardManagerImpl.REGEX_VALID_PHONE_NUMBER, slotRegex.captured)

    }

}