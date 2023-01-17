package com.clonect.feeltalk.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.usecase.LogInWithGoogleUseCase
import com.clonect.feeltalk.domain.usecase.SignUpWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val logInWithGoogleUseCase: LogInWithGoogleUseCase,
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase
): ViewModel() {

    suspend fun fetchGoogleAuthInfo(authCode: String) = withContext(viewModelScope.coroutineContext) {
        logInWithGoogleUseCase(authCode)
    }

    suspend fun signUp(signUpEmailRequest: SignUpEmailRequest) = withContext(viewModelScope.coroutineContext) {
        signUpWithEmailUseCase(signUpEmailRequest)
    }

}