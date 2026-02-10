package com.clonect.feeltalk.release_presentation.ui.mainNavigation.myPage.setting.lockSetting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.usecase.account.CheckAccountLockedFlowUseCase
import com.clonect.feeltalk.release_domain.usecase.account.UnlockAccountUseCase
import com.clonect.feeltalk.release_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockSettingViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val checkAccountLockedFlowUseCase: CheckAccountLockedFlowUseCase,
    private val unlockAccountUseCase: UnlockAccountUseCase,
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
): ViewModel() {

    private val defaultErrorMessage = context.getString(R.string.pillowtalk_default_error_message)

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _lockEnabled = MutableStateFlow(false)
    val lockEnabled = _lockEnabled.asStateFlow()

    init {
        collectCheckAccountLocked()
    }

    private fun collectCheckAccountLocked() = viewModelScope.launch {
        checkAccountLockedFlowUseCase().collectLatest { result ->
            when (result) {
                is Resource.Success -> {
                    _lockEnabled.value = result.data
                }
                is Resource.Error -> {
                    infoLog("Fail to check account locked: ${result.throwable.localizedMessage}")
                }
            }
        }
    }


    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }


    fun setLockEnabled(enabled: Boolean) = viewModelScope.launch {
        _lockEnabled.value = enabled
    }



    fun unlockAccount() = viewModelScope.launch {
        when (val result = unlockAccountUseCase()) {
            is Resource.Success -> {
                _lockEnabled.value = false
            }
            is Resource.Error -> {
                infoLog("Fail to unlock account: ${result.throwable.localizedMessage}")
                sendErrorMessage(defaultErrorMessage)
            }
        }
    }


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }


}