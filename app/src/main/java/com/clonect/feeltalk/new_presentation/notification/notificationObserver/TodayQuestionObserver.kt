package com.clonect.feeltalk.new_presentation.notification.notificationObserver

import com.clonect.feeltalk.new_domain.model.question.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TodayQuestionObserver {
    companion object {
        private var Instance: TodayQuestionObserver? = null

        fun getInstance(): TodayQuestionObserver {
            if (Instance == null) {
                Instance = TodayQuestionObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _todayQuestion = MutableStateFlow<Question?>(null)
    val todayQuestion = _todayQuestion.asStateFlow()

    fun setTodayQuestion(todayQuestion: Question?) {
        _todayQuestion.value = todayQuestion
    }
}