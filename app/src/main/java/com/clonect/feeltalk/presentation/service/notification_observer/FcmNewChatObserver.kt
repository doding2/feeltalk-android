package com.clonect.feeltalk.presentation.service.notification_observer

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.chat.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FcmNewChatObserver {
    companion object {
        private var Instance: FcmNewChatObserver? = null

        fun getInstance(): FcmNewChatObserver {
            if (Instance == null) {
                Instance = FcmNewChatObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _newChat = MutableStateFlow<Resource<Chat>>(Resource.Loading(true))
    val newChat = _newChat.asStateFlow()

    fun setNewChat(chat: Chat) {
        _newChat.value = Resource.Success(chat)
    }
}