package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCountDto
import com.clonect.feeltalk.new_domain.usecase.challenge.GetAddChallengeFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.GetChallengeCountUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.GetDeleteChallengeFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.completed.SnackbarState
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val getChallengeCountUseCase: GetChallengeCountUseCase,
    private val getAddChallengeFlowUseCase: GetAddChallengeFlowUseCase,
    private val getDeleteChallengeFlowUseCase: GetDeleteChallengeFlowUseCase,
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
) : ViewModel() {

    private val _snackbarState = MutableStateFlow(SnackbarState())
    val snackbarState = _snackbarState.asStateFlow()

    private val _ongoingFragmentScrollToTop = MutableSharedFlow<Boolean>()
    val ongoingFragmentScrollToTop = _ongoingFragmentScrollToTop.asSharedFlow()

    private val _completedFragmentScrollToTop = MutableSharedFlow<Boolean>()
    val completedFragmentScrollToTop = _completedFragmentScrollToTop.asSharedFlow()


    private val _challengeCount = MutableStateFlow<ChallengeCountDto?>(null)
    val challengeCount = _challengeCount.asStateFlow()


    private val observerLock = Mutex()

    init {
        getChallengeCount()
        collectAddChallenge()
        collectDeleteChallenge()
    }

    private fun getChallengeCount() = viewModelScope.launch {
        when (val result = getChallengeCountUseCase()) {
            is Resource.Success -> {
                _challengeCount.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to get challenge count: ${result.throwable.localizedMessage}")
            }
        }
    }



    fun setSnackbarState(state: SnackbarState) {
        _snackbarState.value = state
    }

    fun setOngoingFragmentScrollToTop() = viewModelScope.launch {
        _ongoingFragmentScrollToTop.emit(true)
    }

    fun setCompletedFragmentScrollToTop() = viewModelScope.launch {
        _completedFragmentScrollToTop.emit(true)
    }



    private fun collectAddChallenge() = viewModelScope.launch(Dispatchers.IO) {
        getAddChallengeFlowUseCase().collect {
            observerLock.withLock {
                val original = _challengeCount.value
                if (it.isCompleted) {
                    _challengeCount.value = original?.copy(
                        totalCount = original.totalCount + 1,
                        completedCount = original.completedCount + 1
                    )
                } else {
                    _challengeCount.value = original?.copy(
                        totalCount = original.totalCount + 1,
                        ongoingCount = original.ongoingCount + 1
                    )
                }
            }
        }
    }

    private fun collectDeleteChallenge() = viewModelScope.launch(Dispatchers.IO) {
        getDeleteChallengeFlowUseCase().collect {
            observerLock.withLock {
                val original = _challengeCount.value
                if (it.isCompleted) {
                    _challengeCount.value = original?.copy(
                        totalCount = original.totalCount - 1,
                        completedCount = original.completedCount - 1
                    )
                } else {
                    _challengeCount.value = original?.copy(
                        totalCount = original.totalCount - 1,
                        ongoingCount = original.ongoingCount - 1
                    )
                }
            }
        }
    }


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }
}