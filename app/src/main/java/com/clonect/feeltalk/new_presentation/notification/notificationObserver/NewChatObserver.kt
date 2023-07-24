package com.clonect.feeltalk.new_presentation.notification.notificationObserver

import com.clonect.feeltalk.new_domain.model.chat.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NewChatObserver {
    companion object {
        private var Instance: NewChatObserver? = null

        fun getInstance(): NewChatObserver {
            if (Instance == null) {
                Instance = NewChatObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _newChat = MutableStateFlow<Chat?>(null)
    val newChat = _newChat.asStateFlow()

    fun setNewChat(chat: Chat) {
        _newChat.value = chat
    }
}