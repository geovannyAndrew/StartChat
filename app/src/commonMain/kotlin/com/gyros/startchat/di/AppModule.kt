package com.gyros.startchat.di

import com.gyros.startchat.domain.GetChatHistoryUseCase
import com.gyros.startchat.domain.GetCountryCodesUseCase
import com.gyros.startchat.domain.GetDefaultCountryCodeUseCase
import com.gyros.startchat.domain.GetWhatsAppUriUseCase
import com.gyros.startchat.domain.SaveChatHistoryEntryUseCase
import com.gyros.startchat.domain.SaveDefaultCountryCodeUseCase
import com.gyros.startchat.repositories.ChatHistoryRepository
import com.gyros.startchat.repositories.CountryCodeRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    single<CountryCodeRepository> {
        com.gyros.startchat.repositories.CountryCodeRepositoryImpl(
            get(),
            get()
        )
    }
    single<ChatHistoryRepository> { com.gyros.startchat.repositories.ChatHistoryRepositoryImpl(get()) }

    factoryOf(::GetCountryCodesUseCase)
    factoryOf(::GetDefaultCountryCodeUseCase)
    factoryOf(::GetWhatsAppUriUseCase)
    factoryOf(::SaveDefaultCountryCodeUseCase)
    factoryOf(::SaveChatHistoryEntryUseCase)
    factoryOf(::GetChatHistoryUseCase)
}
