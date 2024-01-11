package com.clonect.feeltalk.new_presentation.ui.mainNavigation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.new_domain.model.chat.PokeChat
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.usecase.chat.AddNewChatCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.question.ChangeTodayQuestionCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetAnswerQuestionFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetTodayQuestionFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetTodayQuestionUseCase
import com.clonect.feeltalk.new_domain.usecase.question.PressForAnswerUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetMySignalCacheFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetMySignalUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetPartnerSignalFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetPartnerSignalUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase,
    private val changeTodayQuestionCacheUseCase: ChangeTodayQuestionCacheUseCase,
    private val pressForAnswerUseCase: PressForAnswerUseCase,
    private val answerQuestionFlowUseCase: GetAnswerQuestionFlowUseCase,
    private val getTodayQuestionFlowUseCase: GetTodayQuestionFlowUseCase,
    private val getMySignalUseCase: GetMySignalUseCase,
    private val getPartnerSignalUseCase: GetPartnerSignalUseCase,
    private val getPartnerSignalFlowUseCase: GetPartnerSignalFlowUseCase,
    private val getMySignalCacheFlowUseCase: GetMySignalCacheFlowUseCase,
    private val addNewChatCacheUseCase: AddNewChatCacheUseCase,
) : ViewModel() {

    private val _todayQuestion = MutableStateFlow<Question?>(null)
    val todayQuestion = _todayQuestion.asStateFlow()

    private val _mySignal = MutableStateFlow(Signal.One)
    val mySignal = _mySignal.asStateFlow()

    private val _partnerSignal = MutableStateFlow(Signal.One)
    val partnerSignal = _partnerSignal.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            getMySignal()
            getPartnerSignal()
            getTodayQuestion()
            collectTodayQuestion()
            collectQuestionAnswer()
            collectPartnerSignal()
            collectMySignal()
        }
    }

    suspend fun getTodayQuestion() {
        when (val result = getTodayQuestionUseCase()) {
            is Resource.Success -> {
                _todayQuestion.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to get today question: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    private fun getMySignal() = viewModelScope.launch {
        getMySignalUseCase().onSuccess {
            _mySignal.value = it
        }.onError {
            infoLog("Fail to get my signal: ${it.localizedMessage}")
        }
    }

    private fun getPartnerSignal() = viewModelScope.launch {
        getPartnerSignalUseCase().onSuccess {
            _partnerSignal.value = it
        }.onError {
            infoLog("Fail to get partner signal: ${it.localizedMessage}")
        }
    }

    fun pressForAnswer(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val questionIndex = _todayQuestion.value?.index ?: return@launch
        when (val result = pressForAnswerUseCase(questionIndex)) {
            is Resource.Success -> {
                addNewChatCacheUseCase(
                    result.data.run {
                        PokeChat(
                            index = index,
                            pageNo = pageIndex,
                            chatSender = "me",
                            isRead = isRead,
                            createAt = createAt,
                            questionIndex = questionIndex
                        )
                    }
                )
                _snackbarMessage.emit(context.getString(R.string.answer_poke_partner_snack_bar))
            }
            is Resource.Error -> {
                infoLog("Fail to answer question: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
                _snackbarMessage.emit(context.getString(R.string.pillowtalk_default_error_message))
            }
        }
    }


    fun setMySignal(signal: Signal) = viewModelScope.launch {
        _mySignal.value = signal
    }


    private fun collectTodayQuestion() = viewModelScope.launch {
        getTodayQuestionFlowUseCase().collect {
            if (it == null) return@collect
            _todayQuestion.value = it
        }
    }

    private fun collectQuestionAnswer() = viewModelScope.launch {
        answerQuestionFlowUseCase().collect { new ->
            val todayQuestion = _todayQuestion.value
            if (new.index == todayQuestion?.index) {
                _todayQuestion.value = todayQuestion.let { old ->
                    old.copy(
                        myAnswer = old.myAnswer ?: new.myAnswer,
                        partnerAnswer = old.partnerAnswer ?: new.partnerAnswer
                    ).also {
                        // init today question cache to null
                        // if you change today question to non-null question in here,
                        // unnecessary additional today question object is inserted at question, question share pages
                        changeTodayQuestionCacheUseCase(null)
                    }
                }
            }
        }
    }

    private fun collectPartnerSignal() = viewModelScope.launch {
        getPartnerSignalFlowUseCase().collect {
            if (it == null) return@collect
            _partnerSignal.value = it
        }
    }

    private fun collectMySignal() = viewModelScope.launch {
        getMySignalCacheFlowUseCase().collect {
            if (it == null) return@collect
            _mySignal.value = it
        }
    }
}