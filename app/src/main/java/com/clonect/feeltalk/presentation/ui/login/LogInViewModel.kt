package com.clonect.feeltalk.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.user.AutoLogInWithGoogleUseCase
import com.clonect.feeltalk.domain.usecase.user.SignUpWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val autoLogInWithGoogleUseCase: AutoLogInWithGoogleUseCase,
    private val signUpWithGoogleUseCase: SignUpWithGoogleUseCase
): ViewModel() {

    suspend fun signInWithGoogle(idToken: String, serverAuthCode: String) = withContext(Dispatchers.IO) {
        val result = signUpWithGoogleUseCase(idToken, serverAuthCode)
        return@withContext when (result) {
            is Resource.Success -> true
            is Resource.Error -> {
                Log.i("LogInFragment", "auto log in error: ${result.throwable.localizedMessage}")
                false
            }
            else -> false
        }
    }

    suspend fun autoLogInWithGoogle(idToken: String) = withContext(Dispatchers.IO) {
        val result = autoLogInWithGoogleUseCase(idToken)
        return@withContext when (result) {
            is Resource.Success -> true
            is Resource.Error -> {
                Log.i("LogInFragment", "auto log in error: ${result.throwable.localizedMessage}")
                false
            }
            else -> false
        }
    }

}