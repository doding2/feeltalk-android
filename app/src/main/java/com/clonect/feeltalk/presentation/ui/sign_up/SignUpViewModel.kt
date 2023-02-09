package com.clonect.feeltalk.presentation.ui.sign_up

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.user.CheckUserInfoIsEnteredUseCase
import com.clonect.feeltalk.domain.usecase.user.CheckUserIsCoupleUseCase
import com.clonect.feeltalk.domain.usecase.user.GetUserInfoUseCase
import com.clonect.feeltalk.domain.usecase.user.SignUpWithGoogleUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val checkUserInfoIsEnteredUseCase: CheckUserInfoIsEnteredUseCase,
    private val checkUserIsCoupleUseCase: CheckUserIsCoupleUseCase,
): ViewModel() {

    private val _isSignUpSuccessful = MutableStateFlow(false)
    val isSignUpSuccessful = _isSignUpSuccessful.asStateFlow()

    private val _isLogInSuccessful = MutableStateFlow(false)
    val isLogInSuccessful = _isLogInSuccessful.asStateFlow()

    private val _isUserInfoEntered = MutableStateFlow(false)
    val isUserInfoEntered = _isUserInfoEntered.asStateFlow()

    private val _isUserCouple = MutableStateFlow(false)
    val isUserCouple = _isUserCouple.asStateFlow()




    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = signUpWithGoogleUseCase(idToken, serverAuthCode, getFcmToken())
         when (result) {
            is Resource.Success -> {
                val dto = result.data
                if (dto.annotation == "signUp") {
                    _isSignUpSuccessful.value = true
                    infoLog("Success to sign up")
                }
                if (dto.annotation == "login") {
                    checkUserInfoIsEntered()
                    infoLog("Success to log in")
                }
            }
            is Resource.Error -> {
                infoLog("sign up error: ${result.throwable.localizedMessage}")
            }
            else -> {
                infoLog("sign up error")
            }
        }
    }


    private fun checkUserInfoIsEntered()= viewModelScope.launch(Dispatchers.IO) {
        val result = checkUserInfoIsEnteredUseCase()
        val isEntered = when (result) {
            is Resource.Success -> result.data
            else -> false
        }
        _isUserInfoEntered.value = isEntered
        infoLog("Is user info entered: ${_isUserInfoEntered.value}")

        if (isEntered) {
            checkUserIsCouple()
        } else {
            _isLogInSuccessful.value = true
        }
    }

    private fun checkUserIsCouple()= viewModelScope.launch(Dispatchers.IO) {
        when (val result = checkUserIsCoupleUseCase()) {
            is Resource.Success -> _isUserCouple.value = result.data.isMatch
            else -> _isUserCouple.value = false
        }
        infoLog("Is user couple: ${_isUserCouple.value}")
        _isLogInSuccessful.value = true
    }


    private suspend fun getFcmToken(): String = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                continuation.resume(it)
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }

}