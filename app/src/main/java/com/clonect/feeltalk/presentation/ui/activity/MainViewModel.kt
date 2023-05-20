package com.clonect.feeltalk.presentation.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.domain.usecase.user.*
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val autoLogInUseCase: AutoLogInUseCase,
    private val autoLogInWithGoogleUseCase: AutoLogInWithGoogleUseCase,
    private val autoLogInWithKakaoUseCase: AutoLogInWithKakaoUseCase,
    private val autoLogInWithNaverUseCase: AutoLogInWithNaverUseCase,
    private val autoLogInWithAppleUseCase: AutoLogInWithAppleUseCase,
    private val checkIsAppleLoggedInUseCase: CheckIsAppleLoggedInUseCase,
    private val checkUserInfoIsEnteredUseCase: CheckUserInfoIsEnteredUseCase,
    private val checkUserIsCoupleUseCase: CheckUserIsCoupleUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPartnerInfoUseCase: GetPartnerInfoUseCase,
    private val getAnniversaryUseCase: GetCoupleAnniversaryUseCase,
    private val getMyProfileImageUrlUseCase: GetMyProfileImageUrlUseCase,
    private val getPartnerProfileImageUrlUseCase: GetPartnerProfileImageUrlUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
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

    fun autoLogIn() = viewModelScope.launch(Dispatchers.IO) {
        when (val result = autoLogInUseCase()) {
            is Resource.Success -> {
                _isLoggedIn.value = true
                checkUserInfoIsEntered()
                infoLog("Success to auto log")
            }
            is Resource.Error -> {
                _isLoggedIn.value = false
                infoLog("Fail to auto log in: ${result.throwable.localizedMessage}")
//                sendToast("로그인에 실패했습니다")
                setReady()
            }
            else -> {
                _isLoggedIn.value = false
                infoLog("Fail to auto log in")
//                sendToast("로그인에 실패했습니다")
                setReady()
            }
        }
    }

    fun autoGoogleLogIn() = viewModelScope.launch(Dispatchers.IO) {
        when (autoLogInUseCase()) {
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
        when (autoLogInUseCase()) {
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

    fun autoNaverLogIn() = viewModelScope.launch(Dispatchers.IO) {
        when (autoLogInUseCase()) {
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

    fun autoAppleLogIn() = viewModelScope.launch(Dispatchers.IO) {
        when (autoLogInUseCase()) {
            is Resource.Success -> {
                _isLoggedIn.value = true
                checkUserInfoIsEntered()
                infoLog("Success to log in with apple")
            }
            else -> {
                _isLoggedIn.value = false
                infoLog("Fail to log in with apple")
                sendToast("로그인에 실패했습니다")
                setReady()
            }
        }
    }

    suspend fun checkIsAppleLoggedIn(): Boolean {
        val result = checkIsAppleLoggedInUseCase()
        return when (result) {
            is Resource.Success -> result.data
            else -> false
        }
    }


    private fun getUserInfo() = CoroutineScope(Dispatchers.IO).launch {
        val result = getUserInfoUseCase()

        if (result is Resource.Success) {
            logInMixpanel(result.data)
        }
        if (result is Resource.Error) {
            sendToast("내 정보를 불러오는데 실패했습니다")
            infoLog("Fail to get user info: ${result.throwable}")
        }
    }

    private suspend fun getCoupleInfo() = CoroutineScope(Dispatchers.IO).launch {
        val partnerInfo = async {
            val infoResult = getPartnerInfoUseCase()
            if (infoResult is Resource.Error) {
                sendToast("애인의 정보를 불러오는데 실패했습니다")
                infoLog("Fail to get partner info: ${infoResult.throwable}")
            }
        }

        val myProfile = async {
            val myMyProfileResult = getMyProfileImageUrlUseCase()
            if (myMyProfileResult is Resource.Error) {
                sendToast("내 프로필 이미지를 불러오는데 실패했습니다")
                infoLog("Fail to get my profile url: ${myMyProfileResult.throwable}")
            }
        }

        val partnerProfile = async {
            val partnerProfileResult = getPartnerProfileImageUrlUseCase()
            if (partnerProfileResult is Resource.Error) {
                sendToast("애인의 프로필 이미지를 불러오는데 실패했습니다")
                infoLog("Fail to get partner profile url: ${partnerProfileResult.throwable}")
            }
        }

        val anniversary = async {
            val anniversaryResult = getAnniversaryUseCase()
            if (anniversaryResult is Resource.Error) {
                sendToast("사귄 첫날 정보를 불러오는데 실패했습니다")
                infoLog("Fail to get partner profile url: ${anniversaryResult.throwable}")
            }
        }

        partnerInfo.await()
        myProfile.await()
        partnerProfile.await()
        anniversary.await()
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

        getUserInfo()
        if (isEntered) {
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
            getCoupleInfo()
        }
        setReady()
    }


    fun setReady() {
        _isReady.value = true
    }

    fun sendToast(message: String) = viewModelScope.launch {
        _toast.emit(message)
    }


    private fun logInMixpanel(userInfo: UserInfo) {
        val mixpanel = getMixpanelAPIUseCase()
        mixpanel.identify(userInfo.email, true)
    }
}