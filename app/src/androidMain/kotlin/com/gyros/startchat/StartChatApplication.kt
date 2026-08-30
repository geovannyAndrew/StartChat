package com.gyros.startchat

import android.app.Application
import com.gyros.startchat.di.appModule
import com.gyros.startchat.di.databaseModule
import com.gyros.startchat.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class StartChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@StartChatApplication)
            modules(appModule, databaseModule, viewModelModule)
        }
    }
}
