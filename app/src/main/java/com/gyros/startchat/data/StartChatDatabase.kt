package com.gyros.startchat.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gyros.startchat.data.models.ChatHistoryEntry

@Database(entities = [ChatHistoryEntry::class], version = 1)
abstract class StartChatDatabase : RoomDatabase() {
    abstract fun chatHistoryDao(): ChatHistoryDao
}
