package com.clonect.feeltalk.new_presentation.service.notification_observer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AcceptRestoringKeysRequestObserver {
    companion object {
        private var Instance: AcceptRestoringKeysRequestObserver? = null

        fun getInstance(): AcceptRestoringKeysRequestObserver {
            if (Instance == null) {
                Instance = AcceptRestoringKeysRequestObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _isPartnerAccepted = MutableStateFlow(false)
    val isPartnerAccepted = _isPartnerAccepted.asStateFlow()

    fun setPartnerAccepted(isPartnerAccepted: Boolean) {
        _isPartnerAccepted.value = isPartnerAccepted
    }
}