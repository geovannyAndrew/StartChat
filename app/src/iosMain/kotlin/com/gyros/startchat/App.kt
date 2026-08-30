package com.gyros.startchat

import com.gyros.startchat.di.appModule
import com.gyros.startchat.di.databaseModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule, databaseModule)
    }
}