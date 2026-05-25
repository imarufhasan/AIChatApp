package com.example.aichatappkotlin.ui.screens.chat


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichatappkotlin.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun setListening(listening: Boolean) {
        _uiState.update { it.copy(isListening = listening) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isLoading) return

        val userMsg = Message(text = text, isFromUser = true)
        val loadingMsg = Message(text = "", isFromUser = false, isLoading = true)

        _uiState.update {
            it.copy(
                messages = it.messages + userMsg + loadingMsg,
                inputText = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            val result = repository.sendMessage(text)
            result.fold(
                onSuccess = { reply ->
                    _uiState.update { state ->
                        val newMessages = state.messages.dropLast(1) + Message(
                            text = reply,
                            isFromUser = false
                        )
                        state.copy(messages = newMessages, isLoading = false)
                    }
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        val newMessages = state.messages.dropLast(1) + Message(
                            text = "⚠️ Error: ${error.localizedMessage ?: "Unknown error"}",
                            isFromUser = false
                        )
                        state.copy(
                            messages = newMessages,
                            isLoading = false,
                            error = error.localizedMessage
                        )
                    }
                }
            )
        }
    }
}