package com.clonect.feeltalk.presentation.ui.today_question

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.usecase.question.SendQuestionAnswerUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodayQuestionViewModel @Inject constructor(
    private val sendQuestionAnswerUseCase: SendQuestionAnswerUseCase,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val homeQuestionReference = MutableStateFlow(Question(""))

    private val _questionStateFlow = MutableStateFlow(Question(""))
    val questionStateFlow = _questionStateFlow.asStateFlow()

    private val _myAnswerStateFlow = MutableStateFlow("")
    val myAnswerStateFlow: StateFlow<String> = _myAnswerStateFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        savedStateHandle.get<Question>("selectedQuestion")?.let {
            setQuestion(it.copy())
            homeQuestionReference.value = it
        }
    }

    private fun setQuestion(question: Question) {
        _questionStateFlow.value = question
    }

    fun setMyAnswer(answer: String) {
        _myAnswerStateFlow.value = answer
        _questionStateFlow.value.myAnswer = answer
    }

    suspend fun sendQuestionAnswer() = withContext(Dispatchers.IO) {
        _isLoading.value = true
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = format.format(Date())
        val question = _questionStateFlow.value.apply {
            myAnswerDate = date
        }
        val result = sendQuestionAnswerUseCase(question)
        _isLoading.value = false
        return@withContext when (result) {
            is Resource.Success -> {
                homeQuestionReference.value.apply {
                    myAnswer = question.myAnswer
                    myAnswerDate = question.myAnswerDate
                }
                true
            }
            is Resource.Error -> {
                infoLog("Fail to send question answer: ${result.throwable.localizedMessage}")
                false
            }
            else -> false
        }
    }

}