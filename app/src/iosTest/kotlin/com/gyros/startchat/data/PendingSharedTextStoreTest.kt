package com.gyros.startchat.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PendingSharedTextStoreTest {

    private val clock = FakeClock(epochMs = 1000L)
    private val store = FakePendingSharedTextStore(clock)

    @Test
    fun read_emptyStore_returnsNull() {
        assertNull(store.read())
    }

    @Test
    fun write_andRead_roundTrip() {
        store.write("hello", timestampEpochMs = 500L)

        val result = store.read()
        assertEquals("hello", result?.text)
        assertEquals(500L, result?.timestampEpochMs)
    }

    @Test
    fun clear_removesEntry() {
        store.write("hello", timestampEpochMs = 500L)
        store.clear()

        assertNull(store.read())
    }

    @Test
    fun takeIfFresh_freshEntry_returnsAndRemoves() {
        clock.setTo(5000L)
        store.write("hello", timestampEpochMs = 4000L) // 1s old

        val result = store.takeIfFresh(maxAgeMs = 10_000L)

        assertEquals("hello", result?.text)
        assertEquals(4000L, result?.timestampEpochMs)
        assertNull(store.read()) // entry was removed
    }

    @Test
    fun takeIfFresh_staleEntry_returnsNullAndClears() {
        clock.setTo(20_000L)
        store.write("hello", timestampEpochMs = 5000L) // 15s old

        val result = store.takeIfFresh(maxAgeMs = 10_000L)

        assertNull(result)
        assertNull(store.read()) // entry was cleared
    }

    @Test
    fun takeIfFresh_emptyStore_returnsNull() {
        val result = store.takeIfFresh(maxAgeMs = 10_000L)

        assertNull(result)
    }

    @Test
    fun takeIfFresh_freshEntry_exactlyAtMaxAge_returnsEntry() {
        clock.setTo(10_001L)
        store.write("hello", timestampEpochMs = 1L) // exactly 10000ms old

        val result = store.takeIfFresh(maxAgeMs = 10_000L)

        assertEquals("hello", result?.text)
    }

    @Test
    fun takeIfFresh_staleEntry_oneOverMaxAge_returnsNull() {
        clock.setTo(10_002L)
        store.write("hello", timestampEpochMs = 1L) // 10001ms old

        val result = store.takeIfFresh(maxAgeMs = 10_000L)

        assertNull(result)
    }

    @Test
    fun takeIfFresh_calledTwice_returnsEntryOnlyOnce() {
        clock.setTo(5000L)
        store.write("hello", timestampEpochMs = 4000L)

        val first = store.takeIfFresh(maxAgeMs = 10_000L)
        val second = store.takeIfFresh(maxAgeMs = 10_000L)

        assertEquals("hello", first?.text)
        assertNull(second)
    }

    @Test
    fun write_overwritesPreviousEntry() {
        store.write("first", timestampEpochMs = 100L)
        store.write("second", timestampEpochMs = 200L)

        val result = store.read()
        assertEquals("second", result?.text)
        assertEquals(200L, result?.timestampEpochMs)
    }
}
