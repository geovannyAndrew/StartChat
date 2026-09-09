package com.gyros.startchat.di

import com.gyros.startchat.screens.history.ChatHistoryViewModel
import com.gyros.startchat.screens.startchat.StartChatViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Shared Koin module providing ViewModels across all platforms. */
val viewModelModule = module {
    viewModel { StartChatViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ChatHistoryViewModel(get(), get()) }
}
