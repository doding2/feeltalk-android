package com.clonect.feeltalk.new_presentation.ui.mainNavigation.myPage.setting.passwordSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordSettingViewModel @Inject constructor(

): ViewModel() {

    private val _lockEnabled = MutableStateFlow(false)
    val lockEnabled = _lockEnabled.asStateFlow()

    private val _isConfirmMode = MutableStateFlow(false)
    val isConfirmMode = _isConfirmMode.asStateFlow()

    private val _isConfirmInvalid = MutableStateFlow(false)
    val isConfirmInvalid = _isConfirmInvalid.asStateFlow()


    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()


    fun setLockEnabled(enabled: Boolean) = viewModelScope.launch {
        _lockEnabled.value = enabled
    }

    fun addPasswordNum(num: Int) = viewModelScope.launch {
        if (_isConfirmMode.value) {
            _isConfirmInvalid.value = false
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

    fun navigateAndConfirmPassword(): Boolean {
        if (_isConfirmMode.value) {
            val password = _password.value
            val confirmPassword = _confirmPassword.value
            if (password == confirmPassword) {
                return true
            }
            if (confirmPassword.length == 4) {
                _isConfirmInvalid.value = true
                _confirmPassword.value = ""
            }
        } else {
            val password = _password.value
            if (password.length == 4) {
                _isConfirmMode.value = true
            }
        }
        return false
    }
}