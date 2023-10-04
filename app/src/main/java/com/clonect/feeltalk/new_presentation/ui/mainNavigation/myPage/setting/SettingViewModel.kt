package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.appSettings.Language
import com.clonect.feeltalk.new_domain.usecase.account.CheckAccountLockedFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.account.LogOutUseCase
import com.clonect.feeltalk.new_presentation.service.FirebaseCloudMessagingService
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
class SettingViewModel @Inject constructor(
    private val checkAccountLockedFlowUseCase: CheckAccountLockedFlowUseCase,
    private val logOutUseCase: LogOutUseCase,
): ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _lockEnabled = MutableStateFlow<Boolean?>(null)
    val lockEnabled = _lockEnabled.asStateFlow()

    private val _language = MutableStateFlow<Language>(Language.Korean)
    val language = _language.asStateFlow()


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

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }

    fun setLockEnabled(enabled: Boolean) {
        _lockEnabled.value = enabled
    }

    fun setLanguage(language: Language) {
        _language.value = language
    }


    fun logOut(onComplete: () -> Unit) = viewModelScope.launch {
        setLoading(true)
        when (val result = logOutUseCase()) {
            is Resource.Success -> {
                FirebaseCloudMessagingService.clearFcmToken()
                onComplete()
            }
            is Resource.Error -> {
                infoLog("Fail to log out: ${result.throwable.localizedMessage}")
            }
        }
        setLoading(false)
    }

}