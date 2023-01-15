package com.clonect.feeltalk.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.usecase.LogInWithGoogle
import com.clonect.feeltalk.domain.usecase.SignUpWithEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val logInWithGoogle: LogInWithGoogle,
    private val signUpWithEmail: SignUpWithEmail
): ViewModel() {

    suspend fun fetchGoogleAuthInfo(authCode: String) = withContext(viewModelScope.coroutineContext) {
        logInWithGoogle(authCode)
    }

    suspend fun signUp(signUpEmailRequest: SignUpEmailRequest) = withContext(viewModelScope.coroutineContext) {
        signUpWithEmail(signUpEmailRequest)
    }

}