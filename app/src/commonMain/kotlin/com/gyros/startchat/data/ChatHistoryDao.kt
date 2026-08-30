package com.gyros.startchat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gyros.startchat.data.models.ChatHistoryEntry

@Dao
interface ChatHistoryDao {
    @Query("SELECT * FROM chat_history ORDER BY timestamp DESC LIMIT 50")
    fun getAll(): List<ChatHistoryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entry: ChatHistoryEntry)
}
