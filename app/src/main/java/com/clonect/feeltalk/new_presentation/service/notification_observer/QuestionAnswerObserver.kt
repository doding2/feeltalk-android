package com.clonect.feeltalk.new_presentation.service.notification_observer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuestionAnswerObserver {
    companion object {
        private var Instance: QuestionAnswerObserver? = null

        fun getInstance(): QuestionAnswerObserver {
            if (Instance == null) {
                Instance = QuestionAnswerObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _isAnswerUpdated = MutableStateFlow<Boolean>(false)
    val isAnswerUpdated = _isAnswerUpdated.asStateFlow()

    fun setAnswerUpdated(isAnswerUpdated: Boolean) {
        _isAnswerUpdated.value = isAnswerUpdated
    }
}