package com.clonect.feeltalk.new_presentation.ui.mainNavigation.answer

import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.new_domain.model.question.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AnswerViewModel @Inject constructor(

) : ViewModel() {

    private val _isReadMode = MutableStateFlow<Boolean>(false)
    val isReadMode = _isReadMode.asStateFlow()

    private val _question = MutableStateFlow<Question?>(null)
    val question = _question.asStateFlow()

    private val _answer = MutableStateFlow("")
    val answer = _answer.asStateFlow()

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()


    fun setReadMode(isReadMode: Boolean) {
        _isReadMode.value = isReadMode
    }

    fun setQuestion(question: Question) {
        _question.value = question
    }

    fun setAnswer(answer: String) {
        _answer.value = answer
    }

    fun setKeyboardUp(isUp: Boolean) {
        _isKeyboardUp.value = isUp
    }
}