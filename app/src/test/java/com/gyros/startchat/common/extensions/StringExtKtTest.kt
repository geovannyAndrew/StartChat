package com.gyros.startchat.common.extensions

import org.junit.Assert.assertTrue
import org.junit.Test

class StringExtKtTest {

    @Test
    fun `isValidBasicPhone with valid full phone number including country code and spaces`() {
        assertTrue("+1 123 456 7890".isValidBasicPhone())
        assertTrue("123-456-7890".isValidBasicPhone())
        assertTrue("123-456-7890".isValidBasicPhone())
        assertTrue("+57 (321) 427 5698".isValidBasicPhone())
    }

}