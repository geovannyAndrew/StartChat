package com.gyros.startchat.data

import android.content.SharedPreferences
import kotlinx.datetime.Clock

class PendingSharedTextStore(
    private val prefs: SharedPreferences,
    private val clock: Clock
) : PendingSharedTextStoreInterface {

    override fun read(): PendingSharedText? {
        val text = prefs.getString(PendingSharedTextStoreInterface.KEY_PENDING_TEXT, null)
            ?: return null
        val timestamp = prefs.getLong(PendingSharedTextStoreInterface.KEY_PENDING_TIMESTAMP, 0L)
        if (timestamp == 0L) return null
        return PendingSharedText(text = text, timestampEpochMs = timestamp)
    }

    override fun write(text: String, timestampEpochMs: Long) {
        prefs.edit()
            .putString(PendingSharedTextStoreInterface.KEY_PENDING_TEXT, text)
            .putLong(PendingSharedTextStoreInterface.KEY_PENDING_TIMESTAMP, timestampEpochMs)
            .apply()
    }

    override fun clear() {
        prefs.edit()
            .remove(PendingSharedTextStoreInterface.KEY_PENDING_TEXT)
            .remove(PendingSharedTextStoreInterface.KEY_PENDING_TIMESTAMP)
            .apply()
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
