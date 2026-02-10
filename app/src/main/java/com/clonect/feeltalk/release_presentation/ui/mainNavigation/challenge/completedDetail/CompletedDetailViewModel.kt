package com.clonect.feeltalk.release_presentation.ui.mainNavigation.challenge.completedDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_domain.usecase.challenge.DeleteChallengeUseCase
import com.clonect.feeltalk.release_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.release_domain.usecase.mixpanel.OpenCompletedChallengeDetailMixpanelUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CompletedDetailViewModel @Inject constructor(
    private val deleteChallengeUseCase: DeleteChallengeUseCase,
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
    private val openCompletedChallengeDetailMixpanelUseCase: OpenCompletedChallengeDetailMixpanelUseCase,
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _challenge = MutableStateFlow<Challenge?>(null)
    val challenge = _challenge.asStateFlow()


    private val _title = MutableStateFlow<String?>(null)
    val title = _title.asStateFlow()

    private val _body = MutableStateFlow<String?>(null)
    val body = _body.asStateFlow()

    private val _deadline = MutableStateFlow(Date())
    val deadline = _deadline.asStateFlow()


    fun initChallenge(challenge: Challenge) {
        _challenge.value = challenge
        _title.value = challenge.title
        _body.value = challenge.body
        _deadline.value = challenge.deadline
    }

    fun setTitle(title: String?) {
        _title.value = title
    }

    fun setBody(body: String?) {
        _body.value = body
    }

    fun setDeadline(deadline: Date) {
        _deadline.value = deadline
    }


    fun deleteChallenge(onSuccess: () -> Unit) = viewModelScope.launch {
        val challenge = _challenge.value ?: return@launch
        _isLoading.value = true
        when (val result = deleteChallengeUseCase(challenge)) {
            is Resource.Success -> {
                onSuccess()
            }
            is Resource.Error -> {
                infoLog("Fail to delete completed challenge: ${result.throwable.localizedMessage}")
            }
        }
        _isLoading.value = false
    }


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }

    fun openCompletedChallengeDetail() = viewModelScope.launch {
        openCompletedChallengeDetailMixpanelUseCase()
    }
}