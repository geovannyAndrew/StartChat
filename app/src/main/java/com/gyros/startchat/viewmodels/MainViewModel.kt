package com.gyros.startchat.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.gyros.startchat.data.models.CountryCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.core.net.toUri
import com.gyros.startchat.repositories.CountryCodeRepository

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CountryCodeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StartChatState())
    val state = _state.asStateFlow()
    private val countryCodes = repository.getCountryCodes()

    fun loadCountryCodes() {
        _state.value = StartChatState(
            countryCodes = countryCodes,
            selectedCountryCode = repository.getDefaultCountryCode()
        )
    }

    fun processText(actionText: String) {
        val cleanedText = actionText.trim().replace(Regex("[ \\-()]"), "")
        if (cleanedText.contains("+")) {
            val uri = "https://wa.me/${cleanedText.replace("+", "")}".toUri()
            _state.value = StartChatState(
                actionUri = uri
            )
        } else {
            val selectedCountryCode = repository.getDefaultCountryCode()
            _state.value = StartChatState(
                countryCodes = countryCodes,
                selectedCountryCode = selectedCountryCode,
                phoneNumber = cleanedText
            )
        }
    }

    fun selectCountryCode(countryCode: CountryCode) {
        repository.saveDefaultCountryCode(countryCode)
    }

    fun startChat(
        phoneNumber: String,
        countryCode: CountryCode
    ) {
        processText(
            actionText = "+${countryCode.code}$phoneNumber"
        )
    }


    data class StartChatState(
        val actionUri: Uri? = null,
        val countryCodes: List<CountryCode> = emptyList(),
        val selectedCountryCode: CountryCode? = null,
        val phoneNumber: String = ""
    )
}