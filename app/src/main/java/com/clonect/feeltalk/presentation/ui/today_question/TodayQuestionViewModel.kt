package com.clonect.feeltalk.presentation.ui.today_question

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.question.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TodayQuestionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _questionStateFlow = MutableStateFlow<Resource<Question>>(Resource.Loading)
    val questionStateFlow = _questionStateFlow.asStateFlow()

    private val _myAnswerStateFlow = MutableStateFlow("")
    val myAnswerStateFlow: StateFlow<String> = _myAnswerStateFlow.asStateFlow()

    fun setQuestion(question: Question) {
        _questionStateFlow.value = Resource.Success(question)
    }

    fun setMyAnswer(answer: String) {
        _myAnswerStateFlow.value = answer
    }

    suspend fun requestPartnerAnswer() {
        // TODO
    }

}