package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.contentsShare

import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.question.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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