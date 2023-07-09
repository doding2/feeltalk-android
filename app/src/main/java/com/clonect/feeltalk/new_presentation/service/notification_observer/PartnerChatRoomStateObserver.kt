package com.clonect.feeltalk.new_presentation.service.notification_observer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PartnerChatRoomStateObserver {
    companion object {
        private var Instance: PartnerChatRoomStateObserver? = null

        fun getInstance(): PartnerChatRoomStateObserver {
            if (Instance == null) {
                Instance = PartnerChatRoomStateObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _isInChat = MutableStateFlow(false)
    val isInChat = _isInChat.asStateFlow()

    fun setInChat(isInChat: Boolean) {
        _isInChat.value = isInChat
    }
}