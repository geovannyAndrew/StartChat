package com.gyros.startchat.data

interface PendingSharedTextStoreInterface {
    fun read(): PendingSharedText?
    fun write(text: String, timestampEpochMs: Long)
    fun clear()
    fun takeIfFresh(maxAgeMs: Long = MAX_AGE_MS): PendingSharedText?

    companion object {
        const val APP_GROUP_ID = "group.com.gyros.startchat"
        const val KEY_PENDING_TEXT = "pendingSharedText"
        const val KEY_PENDING_TIMESTAMP = "pendingSharedTimestamp"
        const val MAX_AGE_MS = 10 * 60 * 1000L // 10 minutes
    }
}
