package com.clonect.feeltalk.new_presentation.ui.chatNavigation.contentsShare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.question.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    // TODO
    fun shareQuestion(onComplete: () -> Unit) = viewModelScope.launch {
        val question = _selectedQuestion.value ?: return@launch
        onComplete()
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