package com.clonect.feeltalk.release_presentation.ui.mainNavigation.chatNavigation.contentsShare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.release_domain.usecase.mixpanel.SetInContentShareMixpanelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentsShareViewModel @Inject constructor(
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
    private val setInContentShareMixpanelUseCase: SetInContentShareMixpanelUseCase,
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


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }

    fun setInContentShare(isInContentShare: Boolean) = viewModelScope.launch {
        setInContentShareMixpanelUseCase(isInContentShare)
    }

}