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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodayQuestionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _questionStateFlow = MutableStateFlow(Question())
    val questionStateFlow = _questionStateFlow.asStateFlow()

    private val _myAnswerStateFlow = MutableStateFlow("")
    val myAnswerStateFlow: StateFlow<String> = _myAnswerStateFlow.asStateFlow()

    init {
        savedStateHandle.get<Question>("selectedQuestion")?.let {
            setQuestion(it)
        }
    }

    fun setQuestion(question: Question) {
        _questionStateFlow.value = question
    }

    fun setMyAnswer(answer: String) {
        val format = SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss", Locale.getDefault())
        val date = format.format(Date())

        _myAnswerStateFlow.value = answer
        _questionStateFlow.value.myAnswer = answer
        _questionStateFlow.value.myAnswerDate = date
    }

    suspend fun requestPartnerAnswer() {
        // TODO
    }

}