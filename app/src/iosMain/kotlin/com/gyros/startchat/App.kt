package com.gyros.startchat

import com.gyros.startchat.di.appModule
import com.gyros.startchat.di.dataModule
import com.gyros.startchat.di.viewModelModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule, dataModule, viewModelModule)
    }
}