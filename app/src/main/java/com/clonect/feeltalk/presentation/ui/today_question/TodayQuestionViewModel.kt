package com.clonect.feeltalk.presentation.ui.today_question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.data.util.Result
import com.clonect.feeltalk.domain.model.question.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TodayQuestionViewModel @Inject constructor(): ViewModel() {

    private val _questionStateFlow = MutableStateFlow<Result<Question>>(Result.Loading)
    val questionStateFlow = _questionStateFlow.asStateFlow()

    private val _myAnswerStateFlow = MutableStateFlow("")
    val myAnswerStateFlow: StateFlow<String> = _myAnswerStateFlow.asStateFlow()

    fun setQuestion(question: Question) {
        _questionStateFlow.value = Result.Success(question)
    }

    fun setMyAnswer(answer: String) {
        _myAnswerStateFlow.value = answer
    }

    suspend fun requestPartnerAnswer() {
        // TODO
    }

}