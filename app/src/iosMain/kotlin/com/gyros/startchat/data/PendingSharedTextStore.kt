package com.gyros.startchat.data

import kotlinx.datetime.Clock
import platform.Foundation.NSUserDefaults

class PendingSharedTextStore(
    private val userDefaults: NSUserDefaults,
    private val clock: Clock
) : PendingSharedTextStoreInterface {

    override fun read(): PendingSharedText? {
        val text = userDefaults.stringForKey(PendingSharedTextStoreInterface.KEY_PENDING_TEXT)
            ?: return null
        val timestamp =
            userDefaults.objectForKey(PendingSharedTextStoreInterface.KEY_PENDING_TIMESTAMP) as? Long
                ?: return null
        return PendingSharedText(text = text, timestampEpochMs = timestamp)
    }

    override fun write(text: String, timestampEpochMs: Long) {
        userDefaults.setObject(text, forKey = PendingSharedTextStoreInterface.KEY_PENDING_TEXT)
        userDefaults.setObject(
            timestampEpochMs,
            forKey = PendingSharedTextStoreInterface.KEY_PENDING_TIMESTAMP
        )
    }

    override fun clear() {
        userDefaults.removeObjectForKey(PendingSharedTextStoreInterface.KEY_PENDING_TEXT)
        userDefaults.removeObjectForKey(PendingSharedTextStoreInterface.KEY_PENDING_TIMESTAMP)
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
