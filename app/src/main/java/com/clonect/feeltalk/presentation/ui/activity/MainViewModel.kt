package com.clonect.feeltalk.presentation.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.user.*
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val autoLogInWithGoogleUseCase: AutoLogInWithGoogleUseCase,
    private val autoLogInWithKakaoUseCase: AutoLogInWithKakaoUseCase,
    private val checkUserInfoIsEnteredUseCase: CheckUserInfoIsEnteredUseCase,
    private val checkUserIsCoupleUseCase: CheckUserIsCoupleUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPartnerInfoUseCase: GetPartnerInfoUseCase,
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _isUserInfoEntered = MutableStateFlow(false)
    val isUserInfoEntered = _isUserInfoEntered.asStateFlow()

    private val _isUserCouple = MutableStateFlow(false)
    val isUserCouple = _isUserCouple.asStateFlow()

    private val _toast = MutableSharedFlow<String>()
    val toast = _toast.asSharedFlow()

    init {

    }


    fun autoGoogleLogIn() = viewModelScope.launch(Dispatchers.IO) {
        when (autoLogInWithGoogleUseCase()) {
            is Resource.Success -> {
                _isLoggedIn.value = true
                checkUserInfoIsEntered()
                infoLog("Success to log in with google")
            }
            else -> {
                _isLoggedIn.value = false
                infoLog("Fail to log in with google")
                sendToast("로그인에 실패했습니다")
                setReady()
            }
        }
    }

    fun autoKakaoLogIn() = viewModelScope.launch(Dispatchers.IO) {
        when (autoLogInWithGoogleUseCase()) {
            is Resource.Success -> {
                _isLoggedIn.value = true
                checkUserInfoIsEntered()
                infoLog("Success to log in with kakao")
            }
            else -> {
                _isLoggedIn.value = false
                infoLog("Fail to log in with kakao")
                sendToast("로그인에 실패했습니다")
                setReady()
            }
        }
    }


    private suspend fun getUserInfo() {
        val result = getUserInfoUseCase()
        if (result is Resource.Success) {
            infoLog("Success to get user info: ${result.data}")
        }
        if (result is Resource.Error) {
            sendToast("내 정보를 불러오는데 실패했습니다")
            infoLog("Fail to get user info: ${result.throwable}")
        }
    }

    private suspend fun getPartnerInfo() {
        val result = getPartnerInfoUseCase()
        if (result is Resource.Success) {
            infoLog("Success to get partner info: ${result.data}")
        }
        if (result is Resource.Error) {
            sendToast("애인의 정보를 불러오는데 실패했습니다")
            infoLog("Fail to get partner info: ${result.throwable}")
        }
    }


    private fun checkUserInfoIsEntered()= viewModelScope.launch(Dispatchers.IO) {
        val result = checkUserInfoIsEnteredUseCase()
        val isEntered = when (result) {
            is Resource.Success -> {
                infoLog("Is user info entered: ${result.data}")
                result.data
            }
            else -> {
                _isLoggedIn.value = false
                sendToast("로그인에 실패했습니다")
                infoLog("Fail to check user info is entered")
                false
            }
        }
        _isUserInfoEntered.value = isEntered

        if (isEntered) {
            getUserInfo()
            checkUserIsCouple()
        } else {
            setReady()
        }
    }

    private fun checkUserIsCouple()= viewModelScope.launch(Dispatchers.IO) {
        val isUserCouple = when (val result = checkUserIsCoupleUseCase()) {
            is Resource.Success -> {
                infoLog("Is user couple: ${isUserCouple.value}")
                result.data.isMatch
            }
            else -> {
                _isLoggedIn.value = false
                sendToast("로그인에 실패했습니다")
                infoLog("Fail to check user is couple")
                false
            }
        }
        _isUserCouple.value = isUserCouple

        if (isUserCouple) {
            getPartnerInfo()
        }
        setReady()
    }


    fun setReady() {
        _isReady.value = true
    }

    fun sendToast(message: String) = viewModelScope.launch {
        _toast.emit(message)
    }

}