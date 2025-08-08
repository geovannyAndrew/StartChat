package com.gyros.startchat.common.extensions

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test class for String extension functions.
 */
class StringExtKtTest {

    @Test
    fun `isValidBasicPhone with valid phone numbers`() {
        assertTrue("+1 123 456 7890".isValidBasicPhone())
        assertTrue("123-456-7890".isValidBasicPhone())
        assertTrue("123-456-7890".isValidBasicPhone())
        assertTrue("+57 (321) 427 5698".isValidBasicPhone())
    }

    @Test
    fun `isValidBasicPhone with not valid phone number`() {
        assertFalse("+1 123 456 7890 234324 42342".isValidBasicPhone())
        assertFalse("123-456".isValidBasicPhone())
        assertFalse("a123-456-7890".isValidBasicPhone())
        assertFalse("+57 (321) 427 5698 34234".isValidBasicPhone())
    }

}