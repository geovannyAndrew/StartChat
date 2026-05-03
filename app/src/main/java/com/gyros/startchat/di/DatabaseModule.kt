package com.gyros.startchat.di

import android.content.Context
import androidx.room.Room
import com.gyros.startchat.data.ChatHistoryDao
import com.gyros.startchat.data.StartChatDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStartChatDatabase(@ApplicationContext context: Context): StartChatDatabase =
        Room.databaseBuilder(context, StartChatDatabase::class.java, "start_chat_db")
            .build()

    @Provides
    fun provideChatHistoryDao(database: StartChatDatabase): ChatHistoryDao =
        database.chatHistoryDao()
}
