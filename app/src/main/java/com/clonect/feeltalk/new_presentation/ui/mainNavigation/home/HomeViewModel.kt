package com.clonect.feeltalk.new_presentation.ui.mainNavigation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.usecase.question.ChangeTodayQuestionCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetTodayQuestionUseCase
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.QuestionAnswerObserver
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.TodayQuestionObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase,
    private val changeTodayQuestionCacheUseCase: ChangeTodayQuestionCacheUseCase,
) : ViewModel() {

    private val _todayQuestion = MutableStateFlow<Question?>(null)
    val todayQuestion = _todayQuestion.asStateFlow()

    private val _mySignal = MutableStateFlow(Signal.Seduce)
    val mySignal = _mySignal.asStateFlow()

    private val _partnerSignal = MutableStateFlow(Signal.Seduce)
    val partnerSignal = _partnerSignal.asStateFlow()

    init {
        getTodayQuestion()
        collectTodayQuestion()
        collectQuestionAnswer()
    }

    fun getTodayQuestion() = viewModelScope.launch {
        when (val result = getTodayQuestionUseCase()) {
            is Resource.Success -> {
                _todayQuestion.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to get today question: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }


    fun setMySignal(signal: Signal) {
        _mySignal.value = signal
    }

    fun setPartnerSignal(signal: Signal) {
        _partnerSignal.value = signal
    }


    private fun collectTodayQuestion() = viewModelScope.launch {
        TodayQuestionObserver
            .getInstance()
            .setTodayQuestion(null)
        TodayQuestionObserver
            .getInstance()
            .todayQuestion
            .collectLatest {
                if (it == null) return@collectLatest
                _todayQuestion.value = it
            }
    }

    private fun collectQuestionAnswer() = viewModelScope.launch {
        QuestionAnswerObserver
            .getInstance()
            .setAnsweredQuestion(null)
        QuestionAnswerObserver
            .getInstance()
            .answeredQuestion
            .collect { new ->
                if (new == null) return@collect

                if (new.index == _todayQuestion.value?.index) {
                    _todayQuestion.value = _todayQuestion.value?.let { old ->
                        old.copy(
                            myAnswer = old.myAnswer ?: new.myAnswer,
                            partnerAnswer = old.partnerAnswer ?: new.partnerAnswer
                        ).also {
                            changeTodayQuestionCacheUseCase(it)
                        }
                    }

                }
            }
    }
}