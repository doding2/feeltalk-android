package com.clonect.feeltalk.new_presentation.notification.notificationObserver

import com.clonect.feeltalk.new_domain.model.question.Question
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

    private val _answeredQuestion = MutableStateFlow<Question?>(null)
    val answeredQuestion = _answeredQuestion.asStateFlow()

    fun setAnsweredQuestion(question: Question?) {
        _answeredQuestion.value = question
    }
}