package com.gyros.startchat.screens.history

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyros.startchat.data.models.ChatHistoryEntry
import com.gyros.startchat.domain.GetChatHistoryUseCase
import com.gyros.startchat.domain.GetWhatsAppUriUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHistoryViewModel @Inject constructor(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val getWhatsAppUriUseCase: GetWhatsAppUriUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChatHistoryState(onEntryClicked = ::openWhatsApp))
    val state = _state.asStateFlow()

    private val _events = Channel<Events>()
    val events = _events.receiveAsFlow()

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(entries = getChatHistoryUseCase()) }
        }
    }

    private fun openWhatsApp(entry: ChatHistoryEntry) {
        viewModelScope.launch {
            _events.send(Events.OpenWhatsApp(getWhatsAppUriUseCase(entry.phoneNumber)))
        }
    }

    data class ChatHistoryState(
        val entries: List<ChatHistoryEntry> = emptyList(),
        val onEntryClicked: ((ChatHistoryEntry) -> Unit)? = null
    )

    sealed class Events {
        class OpenWhatsApp(val uri: Uri) : Events()
    }
}
