package com.gyros.startchat.screens.startchat

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyros.startchat.data.models.CountryCode
import com.gyros.startchat.repositories.CountryCodeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartChatViewModel @Inject constructor(
    private val repository: CountryCodeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StartChatState())
    val state = _state.asStateFlow()

    private val _events = Channel<Events>()
    val events = _events.receiveAsFlow()

    private val countryCodes = repository.getCountryCodes()

    fun loadCountryCodes() {
        _state.value = StartChatState(
            countryCodes = countryCodes,
            selectedCountryCode = repository.getDefaultCountryCode(),
            onStartChat = ::startChat,
            onCountryCodeSelected = ::selectCountryCode
        )
    }

    fun processText(actionText: String) {
        viewModelScope.launch {
            val cleanedText = actionText.trim().replace(Regex("[ \\-()]"), "")
            if (cleanedText.contains("+")) {
                val uri = "https://wa.me/${cleanedText.replace("+", "")}".toUri()
                _events.send(
                    Events.StartIntentAction(
                        uri = uri
                    )
                )
            } else {
                val selectedCountryCode = repository.getDefaultCountryCode()
                _state.value = StartChatState(
                    countryCodes = countryCodes,
                    selectedCountryCode = selectedCountryCode,
                    phoneNumber = cleanedText,
                    onStartChat = ::startChat,
                    onCountryCodeSelected = ::selectCountryCode
                )
            }
        }
    }

    private fun selectCountryCode(countryCode: CountryCode) {
        repository.saveDefaultCountryCode(countryCode)
    }

    private fun startChat(
        countryCode: CountryCode?,
        phoneNumber: String,
    ) {
        processText(
            actionText = "+${countryCode?.code}$phoneNumber"
        )
    }


    data class StartChatState(
        val countryCodes: List<CountryCode> = emptyList(),
        val selectedCountryCode: CountryCode? = null,
        val phoneNumber: String = "",
        val onStartChat: (CountryCode?, String) -> Unit = { _, _ -> },
        val onCountryCodeSelected: (CountryCode) -> Unit = {}
    )

    sealed class Events {
        class StartIntentAction(
            val uri: Uri
        ) : Events()
    }
}