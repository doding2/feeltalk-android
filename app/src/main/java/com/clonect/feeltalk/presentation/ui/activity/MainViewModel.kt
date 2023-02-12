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
                infoLog("Success to log in")
            }
            else -> {
                _isLoggedIn.value = false
                infoLog("Fail to log in")
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
            is Resource.Success -> result.data
            else -> {
                _isLoggedIn.value = false
                false
            }
        }
        _isUserInfoEntered.value = isEntered
        infoLog("Is user info entered: ${_isUserInfoEntered.value}")

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
                _isUserCouple.value = result.data.isMatch
                result.data.isMatch
            }
            else -> {
                _isLoggedIn.value = false
                _isUserCouple.value = false
                false
            }
        }
        infoLog("Is user couple: $isUserCouple")

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