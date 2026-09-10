package com.gyros.startchat.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.gyros.startchat.data.models.ChatHistoryEntry

/** Room database containing the chat history table. */
@Database(entities = [ChatHistoryEntry::class], version = 1, exportSchema = true)
@ConstructedBy(StartChatDatabaseConstructor::class)
abstract class StartChatDatabase : RoomDatabase() {
    abstract fun chatHistoryDao(): ChatHistoryDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object StartChatDatabaseConstructor : RoomDatabaseConstructor<StartChatDatabase> {
    override fun initialize(): StartChatDatabase
}
