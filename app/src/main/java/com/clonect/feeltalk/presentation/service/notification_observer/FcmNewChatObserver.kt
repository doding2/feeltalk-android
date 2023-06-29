package com.clonect.feeltalk.presentation.service.notification_observer

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat2
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

    private val _newChat2 = MutableStateFlow<Resource<Chat2>>(Resource.Success(Chat2(
        id = 0,
        question = "",
        owner = "",
        message = "",
        date = "",
        isAnswer = false))
    )
    val newChat = _newChat2.asStateFlow()

    fun setNewChat(chat2: Chat2) {
        _newChat2.value = Resource.Success(chat2)
    }
}