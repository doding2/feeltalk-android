package com.clonect.feeltalk.new_presentation.notification.observer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateCoupleObserver {
    companion object {
        private var Instance: CreateCoupleObserver? = null

        fun getInstance(): CreateCoupleObserver {
            if (Instance == null) {
                Instance = CreateCoupleObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _isCoupleCreated = MutableStateFlow(false)
    val isCoupleCreated = _isCoupleCreated.asStateFlow()

    fun setCoupleCreated(isCreated: Boolean) {
        _isCoupleCreated.value = isCreated
    }
}