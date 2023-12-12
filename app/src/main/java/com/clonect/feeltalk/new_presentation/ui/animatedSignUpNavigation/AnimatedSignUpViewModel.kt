package com.clonect.feeltalk.new_presentation.ui.animatedSignUpNavigation

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.new_domain.usecase.newAccount.RequestAdultAuthCodeUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.RetryRequestAdultAuthCodeUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.VerifyAdultAuthCodeUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by doding2 on 2023/11/18.
 */
@HiltViewModel
class AnimatedSignUpViewModel @Inject constructor(
    private val requestAdultAuthCodeUseCase: RequestAdultAuthCodeUseCase,
    private val retryRequestAdultAuthCodeUseCase: RetryRequestAdultAuthCodeUseCase,
    private val verifyAdultAuthCodeUseCase: VerifyAdultAuthCodeUseCase,
) : ViewModel() {

    private val _startPage: MutableStateFlow<String> = MutableStateFlow("coupleCode")
    val startPage = _startPage.asStateFlow()


    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _birth: MutableStateFlow<String> = MutableStateFlow("")
    val birth = _birth.asStateFlow()
    
    private val _gender: MutableStateFlow<String> = MutableStateFlow("")
    val gender = _gender.asStateFlow()

    private val _mobileCarrier: MutableStateFlow<Int> = MutableStateFlow(1)
    val mobileCarrier = _mobileCarrier.asStateFlow()

    private val _phoneNumber: MutableStateFlow<String> = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _authCode: MutableStateFlow<String> = MutableStateFlow("")
    val authCode = _authCode.asStateFlow()


    private val _state = MutableStateFlow<AnimatedSignUpState>(AnimatedSignUpState.Start)
    val state = _state.asStateFlow()

    private val _isKeyboardUp: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()

    private val _isAgreementAccepted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAgreementAccepted = _isAgreementAccepted.asStateFlow()

    private var authCodeCountDownTimer: CountDownTimer = object: CountDownTimer(3000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            setAuthCodeRemainingTime(millisUntilFinished)
            setAuthCodeState(authCodeState.value.copy(isTimeOut = false))
        }
        override fun onFinish() {
            setAuthCodeRemainingTime(0)
            setAuthCodeState(authCodeState.value.copy(isTimeOut = true))
            setDoneEnabled(false)
        }
    }
    private val _authCodeRemainingTime: MutableStateFlow<Long> = MutableStateFlow(3000)  // 1000 * 60 * 3
    val authCodeRemainingTime = _authCodeRemainingTime.asStateFlow()

    private val _authCodeState: MutableStateFlow<AuthCodeState> = MutableStateFlow(AuthCodeState())
    val authCodeState = _authCodeState.asStateFlow()

    private val _isPersonInfoInvalid: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isPersonInfoInvalid = _isPersonInfoInvalid.asSharedFlow()

    private val _isDoneEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDoneEnabled = _isDoneEnabled.asStateFlow()

    init {

    }

    fun setStartPage(startPage: String) {
        _startPage.value = startPage
    }


    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }


    fun setName(name: String) {
        _name.value = name
    }

    fun setBirth(birth: String) {
        _birth.value = birth
    }

    fun setGender(gender: String) {
        _gender.value = gender
    }

    fun setMobileCarrier(mobileCarrier: Int) {
        _mobileCarrier.value = mobileCarrier
    }

    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun setAuthCode(authCode: String) {
        _authCode.value = authCode
    }


    fun setState(state: AnimatedSignUpState) {
        _state.value = state
    }

    fun setKeyboardUp(isKeyboardUp: Boolean) {
        _isKeyboardUp.value = isKeyboardUp
    }

    fun setAgreementAccepted(isAccepted: Boolean) {
        _isAgreementAccepted.value = isAccepted
    }

    private fun setAuthCodeRemainingTime(authCodeRemainingTime: Long) {
        _authCodeRemainingTime.value = authCodeRemainingTime
    }

    fun setAuthCodeState(authCodeState: AuthCodeState) = viewModelScope.launch {
        _authCodeState.emit(authCodeState)
    }

    private fun setPersonInfoInvalid(isPersonInfoInvalid: Boolean) = viewModelScope.launch {
        _isPersonInfoInvalid.emit(isPersonInfoInvalid)
    }

    fun setDoneEnabled(isEnabled: Boolean) {
        _isDoneEnabled.value = isEnabled
    }


    fun isEdited(): Boolean {
        return name.value.isNotEmpty()
                || birth.value.isNotEmpty()
                || gender.value.isNotEmpty()
                || phoneNumber.value.isNotEmpty()
    }


    fun requestAuthCode() = viewModelScope.launch {
        if (!isAgreementAccepted.value) {
            setAuthCodeState(authCodeState.value.copy(isAgreementDisagreed = true))
            return@launch
        }

        val isPersonInfoInvalid = name.value.isBlank()
                || birth.value.isBlank() || birth.value.length < 6
                || gender.value.isBlank()
                || phoneNumber.value.isBlank() || phoneNumber.value.length < 11
        if (isPersonInfoInvalid) {
            setPersonInfoInvalid(true)
            return@launch
        }

        val providerId = when (mobileCarrier.value) {
            1 -> "SKT"
            2 -> "KT"
            3 -> "LGU"
            4 -> "SKTMVNO"
            5 -> "KTMVNO"
            6 -> "LGUMVNO"
            else -> return@launch
        }
        val isMillennium = when (gender.value) {
            "1", "2", "5", "6" -> false
            "3", "4", "7", "8" -> true
            else -> return@launch
        }
        val nation = when (gender.value) {
            "1", "2", "3", "4" -> "0"
            "5", "6", "7", "8" -> "1"
            else -> return@launch
        }
        val gender = when (gender.value) {
            "1", "3", "5", "7" -> "1"
            "2", "4", "6", "8" -> "2"
            else -> return@launch
        }
        val birthday = if (isMillennium) "20${birth.value}" else "19${birth.value}"


        authCodeCountDownTimer.cancel()

        val onSuccess = {
            setAuthCodeState(authCodeState.value.copy(isTimeOut = false, isRequested = true, isAuthCodeInvalid = false))
            setDoneEnabled(true)
            authCodeCountDownTimer.start()
        }
        val isRetry = authCodeState.value.isRequested
        if (isRetry) {
            retryRequestAdultAuthCodeUseCase(
                providerId = providerId,
                userName = name.value.trim(),
                userPhone = phoneNumber.value,
                userBirthday = birthday,
                userGender = gender,
                userNation = nation
            ).onError {
                    val message = "Fail to retry request adult auth code: ${it.localizedMessage}"
                    sendErrorMessage(message)
                    infoLog(message)
                }
                .onSuccess { onSuccess() }
        } else {
            requestAdultAuthCodeUseCase(
                providerId = providerId,
                userName = name.value.trim(),
                userPhone = phoneNumber.value,
                userBirthday = birthday,
                userGender = gender,
                userNation = nation
            ).onError {
                    val message = "Fail to request adult auth code: ${it.localizedMessage}"
                    sendErrorMessage(message)
                    infoLog(message)
                }
                .onSuccess { onSuccess() }
        }
    }

    fun verifyAuthCode(onSuccess: () -> Unit) = viewModelScope.launch {
        verifyAdultAuthCodeUseCase(authCode.value)
            .onError {
                val message = "Fail to verify adult auth code: ${it.localizedMessage}"
                sendErrorMessage(message)
                infoLog(message)
            }.onSuccess {
                onSuccess()
            }
    }
}