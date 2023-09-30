package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.lockSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.usecase.account.CheckAccountLockedFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.account.UnlockAccountUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockSettingViewModel @Inject constructor(
    private val checkAccountLockedFlowUseCase: CheckAccountLockedFlowUseCase,
    private val unlockAccountUseCase: UnlockAccountUseCase,
): ViewModel() {

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
                    sendErrorMessage(result.throwable.localizedMessage ?: "Fail to check account locked")
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
                sendErrorMessage(result.throwable.localizedMessage?: "Fail to unlock account")
            }
        }
    }


}