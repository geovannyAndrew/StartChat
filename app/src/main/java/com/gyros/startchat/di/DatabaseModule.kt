package com.gyros.startchat.di

import androidx.room.Room
import com.gyros.startchat.data.ChatHistoryDao
import com.gyros.startchat.data.StartChatDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), StartChatDatabase::class.java, "start_chat_db")
            .build()
    }
    single<ChatHistoryDao> { get<StartChatDatabase>().chatHistoryDao() }
}
