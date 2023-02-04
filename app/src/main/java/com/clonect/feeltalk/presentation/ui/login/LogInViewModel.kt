package com.clonect.feeltalk.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.usecase.user.AutoLogInWithGoogleUseCase
import com.clonect.feeltalk.domain.usecase.user.CheckUserIsCoupleUseCase
import com.clonect.feeltalk.domain.usecase.user.GetUserInfoUseCase
import com.clonect.feeltalk.domain.usecase.user.SignUpWithGoogleUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
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
class LogInViewModel @Inject constructor(
    private val autoLogInWithGoogleUseCase: AutoLogInWithGoogleUseCase,
    private val signUpWithGoogleUseCase: SignUpWithGoogleUseCase,
    private val checkUserIsCoupleUseCase: CheckUserIsCoupleUseCase,
): ViewModel() {

    init {

    }

    suspend fun checkUserIsCouple() = withContext(Dispatchers.IO) {
        val result = checkUserIsCoupleUseCase()
        return@withContext when (result) {
            is Resource.Success -> result.data.isMatch
            is Resource.Error -> {
                Log.i("LogInFragment", "check uer is couple error: ${result.throwable.localizedMessage}")
                false
            }
            else -> false
        }
    }

    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String) = withContext(Dispatchers.IO) {
        val result = signUpWithGoogleUseCase(idToken, serverAuthCode, getFcmToken())
        return@withContext when (result) {
            is Resource.Success -> true
            is Resource.Error -> {
                Log.i("LogInFragment", "auto log in error: ${result.throwable.localizedMessage}")
                false
            }
            else -> false
        }
    }

    suspend fun autoLogInWithGoogle() = withContext(Dispatchers.IO) {
        val result = autoLogInWithGoogleUseCase()
        return@withContext when (result) {
            is Resource.Success -> true
            is Resource.Error -> {
                Log.i("LogInFragment", "auto log in error: ${result.throwable.localizedMessage}")
                false
            }
            else -> false
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
}