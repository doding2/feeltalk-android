package com.clonect.feeltalk.new_presentation.notification.observer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompleteChallengeObserver {
    companion object {
        private var Instance: CompleteChallengeObserver? = null

        fun getInstance(): CompleteChallengeObserver {
            if (Instance == null) {
                Instance = CompleteChallengeObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _isCompleted = MutableStateFlow<Boolean?>(null)
    val isCompleted = _isCompleted.asStateFlow()

    fun setCompleted(isCompleted: Boolean?) {
        _isCompleted.value = isCompleted
    }
}