package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.completedDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import com.clonect.feeltalk.new_domain.usecase.challenge.DeleteChallengeUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.DeleteCompletedChallengeObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CompletedDetailViewModel @Inject constructor(
    private val deleteChallengeUseCase: DeleteChallengeUseCase
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _challenge = MutableStateFlow<Challenge?>(null)
    val challenge = _challenge.asStateFlow()


    private val _category = MutableStateFlow(ChallengeCategory.Place)
    val category = _category.asStateFlow()

    private val _title = MutableStateFlow<String?>(null)
    val title = _title.asStateFlow()

    private val _body = MutableStateFlow<String?>(null)
    val body = _body.asStateFlow()

    private val _deadline = MutableStateFlow(Date())
    val deadline = _deadline.asStateFlow()


    fun initChallenge(challenge: Challenge) {
        _challenge.value = challenge
        _category.value = challenge.category
        _title.value = challenge.title
        _body.value = challenge.body
        _deadline.value = challenge.deadline
    }


    fun setCategory(category: ChallengeCategory) {
        _category.value = category
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
        _isLoading.value = true
        val index = _challenge.value?.index ?: run {
            _isLoading.value = false
            return@launch
        }
        when (val result = deleteChallengeUseCase(index)) {
            is Resource.Success -> {
                DeleteCompletedChallengeObserver
                    .getInstance()
                    .setChallenge(
                        challenge.value
                    )
                onSuccess()
            }
            is Resource.Error -> {
                infoLog("Fail to delete completed challenge: ${result.throwable.localizedMessage}")
            }
        }
        _isLoading.value = false
    }
}