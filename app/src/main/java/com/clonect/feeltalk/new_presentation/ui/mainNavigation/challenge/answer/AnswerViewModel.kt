package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.answer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.usecase.question.AnswerQuestionUseCase
import com.clonect.feeltalk.new_domain.usecase.question.PressForAnswerUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.QuestionAnswerObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnswerViewModel @Inject constructor(
    private val answerQuestionUseCase: AnswerQuestionUseCase,
    private val pressForAnswerUseCase: PressForAnswerUseCase,
) : ViewModel() {

    private val job = MutableStateFlow<Job?>(null)


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

    fun answerQuestion(context: Context, onComplete: () -> Unit = {}) = viewModelScope.launch {
        val index = _question.value?.index ?: return@launch
        val answer = _answer.value
        when (val result = answerQuestionUseCase(index, answer)) {
            is Resource.Success -> {
                QuestionAnswerObserver
                    .getInstance()
                    .setAnsweredQuestion(
                        _question.value?.copy(myAnswer = answer)
                    )
                sendSnackbar("UserName" + context.getString(R.string.answer_done_snack_bar))
                onComplete()
            }
            is Resource.Error -> {
                _message.emit(result.throwable.localizedMessage ?: "질문 답변에 실패했습니다.")
                infoLog("Fail to answer question: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun pressForAnswer(context: Context) = viewModelScope.launch {
        when (val result = pressForAnswerUseCase(_question.value?.index ?: return@launch)) {
            is Resource.Success -> {
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


    fun cancelJob() = viewModelScope.launch {
        job.value?.job
    }

    fun setJob(job: Job) {
        this.job.value = job
    }

    fun clear() {
        _isReadMode.value = false
        _question.value = null
        _answer.value = ""
        _isKeyboardUp.value = false
    }


    private fun collectQuestionAnswer() = viewModelScope.launch {
        QuestionAnswerObserver
            .getInstance()
            .setAnsweredQuestion(null)
        QuestionAnswerObserver
            .getInstance()
            .answeredQuestion
            .collect { new ->
                val old = _question.value
                if (new == null || old == null) return@collect

                if (new.index == old.index && old.partnerAnswer == null && new.partnerAnswer != null) {
                    _question.value = old.copy(partnerAnswer = new.partnerAnswer)
                }
            }
    }
}