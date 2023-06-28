package com.clonect.feeltalk.new_presentation.ui.signUpNavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.usecase.signIn.GetCoupleCodeUseCase
import com.clonect.feeltalk.new_domain.usecase.signIn.SignUpUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpNavigationViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val getCoupleCodeUseCase: GetCoupleCodeUseCase,
) : ViewModel() {

    // note: Common
    private val _signUpProcess = MutableStateFlow(0)
    val signUpProcess = _signUpProcess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    fun setSignUpProcess(process: Int) {
        _signUpProcess.value = process.coerceIn(0, 80)
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun clear() {
        _signUpProcess.value = 0
        _isSensitiveInfoAgreed.value = false
        _isPrivacyInfoAgreed.value = false
        _nickname.value = ""
        _partnerCoupleCode.value = null
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

    fun signUp() = viewModelScope.launch(Dispatchers.IO) {
        setLoading(true)
        when (val result = signUpUseCase(_nickname.value)) {
            is Resource.Success -> {
                getCoupleCode()
                setNicknameProcessed(true)
            }
            is Resource.Error -> {
                setNicknameProcessed(false)
                infoLog("회원가입 실패:${result.throwable.stackTrace.joinToString("\n")}")
                result.throwable.localizedMessage?.let { sendErrorMessage(it) }
            }
        }
        setLoading(false)
    }


    // note: Couple Code Fragment
    private val _coupleCode = MutableStateFlow<String?>(null)
    val coupleCode = _coupleCode.asStateFlow()

    private suspend fun getCoupleCode() = withContext(Dispatchers.IO) {
        when (val result = getCoupleCodeUseCase()) {
            is Resource.Success -> {
                _coupleCode.value = result.data.generateCode
            }
            is Resource.Error -> {
                _coupleCode.value = null
                infoLog("커플코드 로딩 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                result.throwable.localizedMessage?.let { sendErrorMessage(it) }
            }
        }
    }


    // note: Couple Connect Bottom Sheet
    private val _partnerCoupleCode = MutableStateFlow<String?>(null)
    val partnerCoupleCode = _partnerCoupleCode.asStateFlow()

    private val _isCoupleConnected = MutableSharedFlow<Boolean>()
    val isCoupleConnected = _isCoupleConnected.asSharedFlow()

    fun setPartnerCoupleCode(code: String) {
        _partnerCoupleCode.value = code
    }

    // TODO
    fun matchCoupleCode() = viewModelScope.launch {
        val partnerCode = _partnerCoupleCode.value ?: return@launch

        _isCoupleConnected.emit(true)
    }
}