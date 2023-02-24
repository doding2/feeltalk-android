package com.clonect.feeltalk.presentation.ui.today_question

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.usecase.question.GetQuestionAnswersUseCase
import com.clonect.feeltalk.domain.usecase.question.SendQuestionAnswerUseCase
import com.clonect.feeltalk.domain.usecase.user.GetPartnerInfoUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodayQuestionViewModel @Inject constructor(
    private val sendQuestionAnswerUseCase: SendQuestionAnswerUseCase,
    private val getQuestionAnswersUseCase: GetQuestionAnswersUseCase,
    private val getPartnerInfoUseCase: GetPartnerInfoUseCase,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val homeQuestionReference = MutableStateFlow(Question(""))

    private val _questionStateFlow = MutableStateFlow(Question(""))
    val questionStateFlow = _questionStateFlow.asStateFlow()

    private val _myAnswerStateFlow = MutableStateFlow("")
    val myAnswerStateFlow: StateFlow<String> = _myAnswerStateFlow.asStateFlow()

    private val _partnerAnswer = MutableStateFlow<String?>(null)
    val partnerAnswer = _partnerAnswer.asStateFlow()

    private val _partnerInfo = MutableStateFlow<UserInfo?>(null)
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        savedStateHandle.get<Question>("selectedQuestion")?.let {
            setQuestion(it.copy())
            homeQuestionReference.value = it
        }
        getQuestionAnswers()
        getPartnerInfo()
    }

    private fun getQuestionAnswers() = viewModelScope.launch(Dispatchers.IO) {
        val result = getQuestionAnswersUseCase(_questionStateFlow.value.question)
        when (result) {
            is Resource.Success -> {
                _partnerAnswer.value = result.data.partner ?: ""
            }
            is Resource.Error -> {
                infoLog("Fail to get question answers: ${_questionStateFlow.value.question}, error: ${result.throwable.localizedMessage}")
                _partnerAnswer.value = null
            }
            else -> {
                infoLog("Fail to get question answers: ${_questionStateFlow.value.question}")
                _partnerAnswer.value = null
            }
        }
    }

    private fun getPartnerInfo() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPartnerInfoUseCase()
        when (result) {
            is Resource.Success -> {
                _partnerInfo.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to get partner info: ${result.throwable.localizedMessage}")
            }
            else -> {
                infoLog("Fail to get partner info")
            }
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