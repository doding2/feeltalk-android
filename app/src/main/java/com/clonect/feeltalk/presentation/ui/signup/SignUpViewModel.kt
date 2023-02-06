package com.clonect.feeltalk.presentation.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.user.SignUpWithGoogleUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithGoogleUseCase: SignUpWithGoogleUseCase,
): ViewModel() {

    init {

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

    private suspend fun getFcmToken(): String = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                continuation.resume(it)
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }
}