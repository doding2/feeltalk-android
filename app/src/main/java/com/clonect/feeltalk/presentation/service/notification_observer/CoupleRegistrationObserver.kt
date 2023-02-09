package com.clonect.feeltalk.presentation.service.notification_observer

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CoupleRegistrationObserver {
    companion object {
        private var Instance: CoupleRegistrationObserver? = null

        fun getInstance(): CoupleRegistrationObserver {
            if (Instance == null) {
                Instance = CoupleRegistrationObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _isCoupleRegistrationCompleted = MutableStateFlow(false)
    val isCoupleRegistrationCompleted = _isCoupleRegistrationCompleted.asStateFlow()

    fun setCoupleRegistrationCompleted(isCompleted: Boolean) {
        _isCoupleRegistrationCompleted.value = isCompleted
    }
}