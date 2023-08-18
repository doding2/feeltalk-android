package com.clonect.feeltalk.new_presentation.ui.chatNavigation.contentsShare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.chat.QuestionChat
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.usecase.question.ShareQuestionUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.NewChatObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentsShareViewModel @Inject constructor(
    private val shareQuestionUseCase: ShareQuestionUseCase,
): ViewModel() {

    private val _selectedQuestion = MutableStateFlow<Question?>(null)
    val selectedQuestion = _selectedQuestion.asStateFlow()

    private val _selectedChallenge = MutableStateFlow<Challenge?>(null)
    val selectedChallenge = _selectedChallenge.asStateFlow()


    fun getShareQuestionEnabled() = _selectedQuestion.value != null

    fun selectQuestion(question: Question?) {
        _selectedQuestion.value = question
        _selectedChallenge.value = null
    }

    fun shareQuestion(onComplete: () -> Unit) = viewModelScope.launch {
        val question = _selectedQuestion.value ?: return@launch
        onComplete()
        when (val result = shareQuestionUseCase(question.index)) {
            is Resource.Success -> {
                val questionChat = result.data.run {
                    QuestionChat(
                        index = index,
                        pageNo = pageNo,
                        chatSender = "me",
                        isRead = isRead,
                        createAt = createAt,
                        question = coupleQuestion.index
                    )
                }

                NewChatObserver.getInstance().setNewChat(questionChat)
            }
            is Resource.Error -> {
                infoLog("질문 공유 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }


    fun getShareChallengeEnabled() = _selectedChallenge.value != null

    fun selectedChallenge(challenge: Challenge?) {
        _selectedQuestion.value = null
        _selectedChallenge.value = challenge
    }

    // TODO
    fun shareChallenge(onComplete: () -> Unit) = viewModelScope.launch {
        val challenge = _selectedChallenge.value ?: return@launch
        onComplete()
    }


}