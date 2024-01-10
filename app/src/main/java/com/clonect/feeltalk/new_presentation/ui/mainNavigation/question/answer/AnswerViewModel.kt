package com.clonect.feeltalk.new_presentation.ui.mainNavigation.question.answer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.new_domain.model.chat.AnswerChat
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.PokeChat
import com.clonect.feeltalk.new_domain.model.chat.QuestionChat
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.usecase.chat.AddNewChatCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.partner.GetPartnerInfoFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.question.AnswerQuestionUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetAnswerQuestionFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.question.PressForAnswerUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AnswerViewModel @Inject constructor(
    private val answerQuestionUseCase: AnswerQuestionUseCase,
    private val pressForAnswerUseCase: PressForAnswerUseCase,
    private val getAnswerQuestionFlowUseCase: GetAnswerQuestionFlowUseCase,
    private val getPartnerInfoFlowUseCase: GetPartnerInfoFlowUseCase,
    private val addNewChatCacheUseCase: AddNewChatCacheUseCase,
) : ViewModel() {

    private val _isReadMode = MutableStateFlow(false)
    val isReadMode = _isReadMode.asStateFlow()

    private val _question = MutableStateFlow<Question?>(null)
    val question = _question.asStateFlow()

    private val _answer = MutableStateFlow("")
    val answer = _answer.asStateFlow()

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    init {
        collectQuestionAnswer()
    }

    fun answerQuestion(context: Context, onComplete: () -> Unit = {}) = viewModelScope.launch(
        Dispatchers.IO) {
        val question = _question.value ?: return@launch
        val answer = _answer.value
        when (val result = answerQuestionUseCase(question, answer)) {
            is Resource.Success -> {
                getPartnerInfoFlowUseCase().first().onSuccess {
                    sendSnackbar(it.nickname + context.getString(R.string.answer_done_snack_bar))
                }.onError {
                    infoLog("Fail to get partner info when answering question completed: ${it.localizedMessage}")
                }

                val now = Date()
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

                addNewChatCacheUseCase(
                    if (question.partnerAnswer == null) {
                        AnswerChat(
                            index = 0,
                            pageNo = 0,
                            chatSender = "me",
                            isRead = false,
                            createAt = format.format(now),
                            question = question.copy(myAnswer = answer)
                        )
                    } else {
                        QuestionChat(
                            index = 0,
                            pageNo = 0,
                            chatSender = "me",
                            isRead = false,
                            createAt = format.format(now),
                            question = question.copy(myAnswer = answer)
                        )
                    }
                )
                onComplete()
            }
            is Resource.Error -> {
                _message.emit(result.throwable.localizedMessage ?: "질문 답변에 실패했습니다.")
                infoLog("Fail to answer question: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun pressForAnswer(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val questionIndex = _question.value?.index ?: return@launch
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
                sendSnackbar(context.getString(R.string.answer_poke_partner_snack_bar))
            }
            is Resource.Error -> {
                sendSnackbar(result.throwable.localizedMessage ?: "질문 답변에 실패했습니다.")
                infoLog("Fail to answer question: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }


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

    fun sendSnackbar(message: String) = viewModelScope.launch {
        _message.emit(message)
    }


    fun clear() {
        _isReadMode.value = false
        _question.value = null
        _answer.value = ""
        _isKeyboardUp.value = false
    }


    private fun collectQuestionAnswer() = viewModelScope.launch {
        getAnswerQuestionFlowUseCase().collect { new ->
            val old = _question.value ?: return@collect
            if (old.index == new.index) {
                _question.value = old.copy(myAnswer = new.myAnswer, partnerAnswer = new.partnerAnswer)
            }
        }
    }
}