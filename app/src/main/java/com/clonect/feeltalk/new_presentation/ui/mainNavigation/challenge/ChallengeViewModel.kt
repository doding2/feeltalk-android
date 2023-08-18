package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCountDto
import com.clonect.feeltalk.new_domain.usecase.challenge.GetChallengeCountUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.AddCompletedChallengeObserver
import com.clonect.feeltalk.new_presentation.notification.observer.AddOngoingChallengeObserver
import com.clonect.feeltalk.new_presentation.notification.observer.DeleteCompletedChallengeObserver
import com.clonect.feeltalk.new_presentation.notification.observer.DeleteOngoingChallengeObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {

    private val _ongoingFragmentScrollToTop = MutableSharedFlow<Boolean>()
    val ongoingFragmentScrollToTop = _ongoingFragmentScrollToTop.asSharedFlow()

    private val _completedFragmentScrollToTop = MutableSharedFlow<Boolean>()
    val completedFragmentScrollToTop = _completedFragmentScrollToTop.asSharedFlow()


    private val _challengeCount = MutableStateFlow(ChallengeCountDto(0, 0, 0))
    val challengeCount = _challengeCount.asStateFlow()


    private val observerLock = Mutex()

    init {
        getChallengeCount()
        collectAddOngoingChallenge()
        collectDeleteOngoingChallenge()
        collectAddCompletedChallenge()
        collectDeleteCompletedChallenge()
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



    fun setOngoingFragmentScrollToTop() = viewModelScope.launch {
        _ongoingFragmentScrollToTop.emit(true)
    }

    fun setCompletedFragmentScrollToTop() = viewModelScope.launch {
        _completedFragmentScrollToTop.emit(true)
    }



    private fun collectAddOngoingChallenge() = viewModelScope.launch {
        AddOngoingChallengeObserver
            .getInstance()
            .setChallenge(null)
        AddOngoingChallengeObserver
            .getInstance()
            .challenge
            .collect {
                observerLock.withLock {
                    if (it == null) return@collect
                    val original = _challengeCount.value
                    _challengeCount.value = original.copy(
                        totalCount = original.totalCount + 1,
                        ongoingCount = original.ongoingCount + 1
                    )
                }
            }
    }

    private fun collectDeleteOngoingChallenge() = viewModelScope.launch {
        DeleteOngoingChallengeObserver
            .getInstance()
            .setChallenge(null)
        DeleteOngoingChallengeObserver
            .getInstance()
            .challenge
            .collect {
                observerLock.withLock {
                    if (it == null) return@collect
                    val original = _challengeCount.value
                    _challengeCount.value = original.copy(
                        totalCount = original.totalCount - 1,
                        ongoingCount = original.ongoingCount - 1
                    )
                }
            }
    }

    private fun collectAddCompletedChallenge() = viewModelScope.launch {
        AddCompletedChallengeObserver
            .getInstance()
            .setChallenge(null)
        AddCompletedChallengeObserver
            .getInstance()
            .challenge
            .collect {
                observerLock.withLock {
                    if (it == null) return@collect
                    val original = _challengeCount.value
                    _challengeCount.value = original.copy(
                        totalCount = original.totalCount + 1,
                        completedCount = original.completedCount + 1
                    )
                }
            }
    }

    private fun collectDeleteCompletedChallenge() = viewModelScope.launch {
        DeleteCompletedChallengeObserver
            .getInstance()
            .setChallenge(null)
        DeleteCompletedChallengeObserver
            .getInstance()
            .challenge
            .collect {
                observerLock.withLock {
                    if (it == null) return@collect
                    val original = _challengeCount.value
                    _challengeCount.value = original.copy(
                        totalCount = original.totalCount - 1,
                        completedCount = original.completedCount - 1
                    )
                }
            }
    }
}