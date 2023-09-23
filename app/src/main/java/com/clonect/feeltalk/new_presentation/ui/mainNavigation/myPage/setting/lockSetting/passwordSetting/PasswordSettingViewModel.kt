package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.lockSetting.passwordSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.usecase.account.UpdateAccountLockPasswordUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordSettingViewModel @Inject constructor(
    private val updateAccountLockPasswordUseCase: UpdateAccountLockPasswordUseCase
): ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()



    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val  _lockEnabled = MutableStateFlow(false)
    val lockEnabled = _lockEnabled.asStateFlow()

    private val _isConfirmMode = MutableStateFlow(false)
    val isConfirmMode = _isConfirmMode.asStateFlow()

    private val _isConfirmInvalid = MutableStateFlow(false)
    val isConfirmInvalid = _isConfirmInvalid.asStateFlow()


    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()



    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }


    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setLockEnabled(enabled: Boolean) = viewModelScope.launch {
        _lockEnabled.value = enabled
    }

    fun addPasswordNum(num: Int) = viewModelScope.launch {
        _isConfirmInvalid.value = false
        if (_isConfirmMode.value) {
            val password = _confirmPassword.value
            if (password.length < 4) {
                _confirmPassword.value = password + num
            }
        } else {
            val password = _password.value
            if (password.length < 4) {
                _password.value = password + num
            }
        }
    }

    fun clearPassword() = viewModelScope.launch {
        if (_isConfirmMode.value) {
            _confirmPassword.value = ""
        } else {
            _password.value = ""
        }
    }

    fun navigateOrConfirmPassword(): Boolean {
        if (_isConfirmMode.value) {
            val password = _password.value
            val confirmPassword = _confirmPassword.value
            if (password == confirmPassword) {
                return true
            }
            if (confirmPassword.length == 4) {
                _isConfirmMode.value = false
                _password.value = ""
                _confirmPassword.value = ""
                _isConfirmInvalid.value = true
            }
        } else {
            val password = _password.value
            if (password.length == 4) {
                _isConfirmMode.value = true
            }
        }
        return false
    }


    fun updatePassword(onComplete: () -> Unit) = viewModelScope.launch {
        setLoading(true)
        val password = _password.value
        if (password.length < 4) {
            setLoading(false)
            return@launch
        }

        when (val result = updateAccountLockPasswordUseCase(password)) {
            is Resource.Success -> {
                onComplete()
            }
            is Resource.Error -> {
                infoLog("Fail to update password: ${result.throwable.localizedMessage}")
                sendErrorMessage(result.throwable.localizedMessage ?: "Fail to update password")
            }
        }
        setLoading(false)
    }
}