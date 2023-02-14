package com.clonect.feeltalk.presentation.ui.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.user.*
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithGoogleUseCase: SignUpWithGoogleUseCase,
    private val signUpWithKakaoUseCase: SignUpWithKakaoUseCase,
    private val signUpWithNaverUseCase: SignUpWithNaverUseCase,
    private val checkUserInfoIsEnteredUseCase: CheckUserInfoIsEnteredUseCase,
    private val checkUserIsCoupleUseCase: CheckUserIsCoupleUseCase,
    private val clearAllTokensUseCase: ClearAllTokensUseCase,
): ViewModel() {

    private val _isSignUpSuccessful = MutableStateFlow(false)
    val isSignUpSuccessful = _isSignUpSuccessful.asStateFlow()

    private val _isLogInSuccessful = MutableStateFlow(false)
    val isLogInSuccessful = _isLogInSuccessful.asStateFlow()

    private val _isUserInfoEntered = MutableStateFlow(false)
    val isUserInfoEntered = _isUserInfoEntered.asStateFlow()

    private val _isUserCouple = MutableStateFlow(false)
    val isUserCouple = _isUserCouple.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toast = MutableSharedFlow<String>()
    val toast = _toast.asSharedFlow()


    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String) = withContext(Dispatchers.IO) {
        setLoading(true)
        val result = signUpWithGoogleUseCase(idToken, serverAuthCode, getFcmToken())
        return@withContext when (result) {
            is Resource.Success -> {
                val dto = result.data
                if (dto.annotation == "signUp") {
                    setLoading(false)
                    _isSignUpSuccessful.value = true
                    infoLog("Success to sign up with google")
                    return@withContext true
                }
                if (dto.annotation == "login" || dto.annotation == "login_noCouple") {
                    checkUserInfoIsEntered()
                    infoLog("Success to log in with google")
                    return@withContext true
                }
                false
            }
            is Resource.Error -> {
                infoLog("sign up error with google: ${result.throwable.localizedMessage}")
                sendToast("구글로 가입하는데 실패했습니다")
                setLoading(false)
                false
            }
            else -> {
                infoLog("sign up error with google")
                sendToast("구글로 가입하는데 실패했습니다")
                setLoading(false)
                false
            }
        }
    }

    suspend fun signUpWithKakao(accessToken: String) = withContext(Dispatchers.IO) {
        setLoading(true)
        val result = signUpWithKakaoUseCase(accessToken, getFcmToken())
        return@withContext when (result) {
            is Resource.Success -> {
                val dto = result.data
                if (dto.annotation == "signUp") {
                    setLoading(false)
                    _isSignUpSuccessful.value = true
                    infoLog("Success to sign up with kakao")
                    return@withContext true
                }
                if (dto.annotation == "login" || dto.annotation == "login_noCouple") {
                    checkUserInfoIsEntered()
                    infoLog("Success to log in with kakao")
                    return@withContext true
                }
                false
            }
            is Resource.Error -> {
                infoLog("sign up error with kakao: ${result.throwable.localizedMessage}")
                sendToast("카카오로 가입하는데 실패했습니다")
                setLoading(false)
                false
            }
            else -> {
                infoLog("sign up error with kakao")
                sendToast("카카오로 가입하는데 실패했습니다")
                setLoading(false)
                false
            }
        }
    }

    suspend fun signUpWithNaver(accessToken: String) = withContext(Dispatchers.IO) {
        setLoading(true)
        val result = signUpWithNaverUseCase(accessToken, getFcmToken())
        return@withContext when (result) {
            is Resource.Success -> {
                val dto = result.data
                if (dto.annotation == "signUp") {
                    setLoading(false)
                    _isSignUpSuccessful.value = true
                    infoLog("Success to sign up with naver")
                    return@withContext true
                }
                if (dto.annotation == "login" || dto.annotation == "login_noCouple") {
                    checkUserInfoIsEntered()
                    infoLog("Success to log in with naver")
                    return@withContext true
                }
                false
            }
            is Resource.Error -> {
                infoLog("sign up error with naver: ${result.throwable.localizedMessage}")
                sendToast("네이버로 가입하는데 실패했습니다")
                setLoading(false)
                false
            }
            else -> {
                infoLog("sign up error with naver")
                sendToast("네이버로 가입하는데 실패했습니다")
                setLoading(false)
                false
            }
        }
    }



    private fun checkUserInfoIsEntered() = viewModelScope.launch(Dispatchers.IO) {
        val result = checkUserInfoIsEnteredUseCase()
        when (result) {
            is Resource.Success -> {
                val isSuccessful = result.data
                if (isSuccessful) {
                    checkUserIsCouple()
                }
                setLoading(false)
                _isUserInfoEntered.value = isSuccessful
                _isLogInSuccessful.value = true
                infoLog("Is user info entered: ${isSuccessful}")
            }
            else -> {
                sendToast("구글로 가입하는데 실패했습니다")
                setLoading(false)
                _isUserInfoEntered.value = false
                _isLogInSuccessful.value = false
                infoLog("Fail to check user info is entered")
            }
        }


    }

    private suspend fun checkUserIsCouple() {
        when (val result = checkUserIsCoupleUseCase()) {
            is Resource.Success -> {
                setLoading(false)
                _isUserCouple.value = result.data.isMatch
                _isLogInSuccessful.value = true
                infoLog("Is user couple: ${result.data.isMatch}")
            }
            else -> {
                sendToast("구글로 가입하는데 실패했습니다")
                setLoading(false)
                _isUserCouple.value = false
                _isLogInSuccessful.value = false
                infoLog("Fail to check user is couple")
            }
        }
    }


    private suspend fun getFcmToken(): String = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                continuation.resume(it)
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }


    suspend fun clearAllTokens(): Boolean {
        val result = clearAllTokensUseCase()
        return when (result) {
            is Resource.Success -> true
            else -> false
        }
    }

    fun sendToast(message: String) = viewModelScope.launch {
        _toast.emit(message)
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

}