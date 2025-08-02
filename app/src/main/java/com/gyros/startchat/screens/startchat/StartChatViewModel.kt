package com.gyros.startchat.screens.startchat

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyros.startchat.common.extensions.hasCountryCode
import com.gyros.startchat.common.extensions.sanitizePhoneNumber
import com.gyros.startchat.data.ClipBoardManager
import com.gyros.startchat.data.models.CountryCode
import com.gyros.startchat.domain.GetCountryCodesUseCase
import com.gyros.startchat.domain.GetDefaultCountryCodeUseCase
import com.gyros.startchat.domain.SaveDefaultCountryCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartChatViewModel @Inject constructor(
    private val saveDefaultCountryCodeUseCase: SaveDefaultCountryCodeUseCase,
    private val getCountryCodesUseCase: GetCountryCodesUseCase,
    private val getDefaultCountryCodeUseCase: GetDefaultCountryCodeUseCase,
    private val clipBoardManager: ClipBoardManager
) : ViewModel() {

    private val _state = MutableStateFlow(StartChatState(
        onEditTextChange = ::cleanText,
        onStartChat = ::startChat,
        onCountryCodeSelected = ::selectCountryCode
    ))
    val state = _state.asStateFlow()

    private val _events = Channel<Events>()
    val events = _events.receiveAsFlow()

    private val countryCodes by lazy { getCountryCodesUseCase() }

    fun start(actionText: String?) {
        actionText?.let {
            processText(it)
        } ?: run {
            loadCountryCodes()
        }
    }

    private fun loadCountryCodes() {
        val phoneNumberFromClipBoard = clipBoardManager.getPhoneNumbersFromClipBoard().firstOrNull()?.sanitizePhoneNumber() // TODO
        _state.update {
            it.copy(
                countryCodes = countryCodes,
                selectedCountryCode = getDefaultCountryCodeUseCase(),
                phoneNumber = phoneNumberFromClipBoard.orEmpty()
            )
        }
    }

    private fun cleanText(text: String) {
        val sanitized = text.sanitizePhoneNumber()
        if (sanitized.hasCountryCode()) {
            _state.update {
                it.copy(
                    countryCodes = null,
                    selectedCountryCode = null,
                    phoneNumber = sanitized
                )
            }
        } else {
            _state.update {
                it.copy(
                    countryCodes = countryCodes,
                    selectedCountryCode = getDefaultCountryCodeUseCase(),
                    phoneNumber = sanitized
                )
            }
        }
    }

    private fun processText(actionText: String) {
        viewModelScope.launch {
            val cleanedText = actionText.sanitizePhoneNumber()
            if (cleanedText.hasCountryCode()) {
                val uri = "https://wa.me/${cleanedText.replace("+", "")}".toUri()
                _events.send(
                    Events.StartIntentAction(
                        uri = uri
                    )
                )
            } else {
                val selectedCountryCode = getDefaultCountryCodeUseCase()
                _state.update {
                    it.copy(
                        countryCodes = countryCodes,
                        selectedCountryCode = selectedCountryCode,
                        phoneNumber = cleanedText,
                    )
                }
            }
        }
    }

    private fun selectCountryCode(countryCode: CountryCode) {
        saveDefaultCountryCodeUseCase(countryCode)
        _state.update {
            it.copy(
                selectedCountryCode = countryCode
            )
        }
    }

    private fun startChat(
        countryCode: CountryCode?,
        phoneNumber: String,
    ) {
        countryCode?.let {
            processText(
                actionText = "+${countryCode.dialCode}$phoneNumber"
            )
        } ?: run {
            processText(
                actionText = phoneNumber
            )
        }
    }

    fun onResume() {
        val numbersFromClipBoard = clipBoardManager.getPhoneNumbersFromClipBoard()
        _state.update {
            it.copy(
                numbersOnClipBoard = numbersFromClipBoard
            )
        }

    }


    data class StartChatState(
        val countryCodes: List<CountryCode>? = null,
        val selectedCountryCode: CountryCode? = null,
        val phoneNumber: String = "",
        val onStartChat: (CountryCode?, String) -> Unit = { _, _ -> },
        val onCountryCodeSelected: (CountryCode) -> Unit = {},
        val onEditTextChange: (String) -> Unit = {},
        val numbersOnClipBoard: List<String>? = null
    )

    sealed class Events {
        class StartIntentAction(
            val uri: Uri
        ) : Events()
    }
}