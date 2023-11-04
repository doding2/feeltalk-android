package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.contentsShare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.chat.QuestionChat
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.usecase.challenge.GetChallengeUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetQuestionUseCase
import com.clonect.feeltalk.new_domain.usecase.question.ShareQuestionUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.NewChatObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContentsShareViewModel @Inject constructor(

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


    fun getShareChallengeEnabled() = _selectedChallenge.value != null

    fun selectChallenge(challenge: Challenge?) {
        _selectedQuestion.value = null
        _selectedChallenge.value = challenge
    }

}