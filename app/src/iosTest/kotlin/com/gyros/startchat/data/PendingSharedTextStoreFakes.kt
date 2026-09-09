package com.gyros.startchat.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FakeClock(private var epochMs: Long = 0L) : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(epochMs)

    fun advanceBy(ms: Long) {
        epochMs += ms
    }

    fun setTo(ms: Long) {
        epochMs = ms
    }
}

class FakePendingSharedTextStore(private val clock: Clock) : PendingSharedTextStoreInterface {
    private var storedText: String? = null
    private var storedTimestamp: Long? = null

    override fun read(): PendingSharedText? {
        val t = storedText ?: return null
        val ts = storedTimestamp ?: return null
        return PendingSharedText(text = t, timestampEpochMs = ts)
    }

    override fun write(text: String, timestampEpochMs: Long) {
        storedText = text
        storedTimestamp = timestampEpochMs
    }

    override fun clear() {
        storedText = null
        storedTimestamp = null
    }

    override fun takeIfFresh(maxAgeMs: Long): PendingSharedText? {
        val entry = read() ?: return null
        val age = clock.now().toEpochMilliseconds() - entry.timestampEpochMs
        return if (age <= maxAgeMs) {
            clear()
            entry
        } else {
            clear()
            null
        }
    }
}
