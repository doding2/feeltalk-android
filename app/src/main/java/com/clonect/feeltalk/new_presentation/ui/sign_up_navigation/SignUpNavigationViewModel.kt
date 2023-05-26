package com.clonect.feeltalk.new_presentation.ui.sign_up_navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpNavigationViewModel @Inject constructor(

) : ViewModel() {

    // note: Common
    private val _signUpProcess = MutableStateFlow(0)
    val signUpProcess = _signUpProcess.asStateFlow()

    fun setSignUpProcess(process: Int) {
        _signUpProcess.value = process.coerceIn(0, 80)
    }


    // note: Agreement Fragment
    private val _isSensitiveInfoAgreed = MutableStateFlow(false)
    val isSensitiveInfoAgreed = _isSensitiveInfoAgreed.asStateFlow()

    private val _isPrivacyInfoAgreed = MutableStateFlow(false)
    val isPrivacyInfoAgreed = _isPrivacyInfoAgreed.asStateFlow()

    private val _isAgreementProcessed = MutableSharedFlow<Boolean>()
    val isAgreementProcessed = _isAgreementProcessed.asSharedFlow()

    fun setSensitiveInfoAgreed(agreed: Boolean) {
        _isSensitiveInfoAgreed.value = agreed
    }
    
    fun setPrivacyInfoAgreed(agreed: Boolean) {
        _isPrivacyInfoAgreed.value = agreed
    }

    fun setAgreementProcessed(processed: Boolean) = viewModelScope.launch {
        _isAgreementProcessed.emit(processed)
    }


    // note: Nickname Fragment
    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private val _isNicknameProcessed = MutableSharedFlow<Boolean>()
    val isNicknameProcessed = _isNicknameProcessed.asSharedFlow()

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }

    fun setNicknameProcessed(processed: Boolean) = viewModelScope.launch {
        _isNicknameProcessed.emit(processed)
    }


    // note: Couple Code Fragment
    private val _coupleCode = MutableStateFlow<String?>("COUPLECODE")
    val coupleCode = _coupleCode.asStateFlow()

}