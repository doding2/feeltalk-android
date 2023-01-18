package com.clonect.feeltalk.presentation.ui.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.domain.model.user.LogInEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.usecase.GetGoogleTokensUseCase
import com.clonect.feeltalk.domain.usecase.LogInWithEmailUseCase
import com.clonect.feeltalk.domain.usecase.SignUpWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val getGoogleTokensUseCase: GetGoogleTokensUseCase,
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase,
    private val logInWithEmailUseCase: LogInWithEmailUseCase,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {


    private val _emailStateFlow = MutableStateFlow("")
    val emailStateFlow = _emailStateFlow.asStateFlow()

    private val _passwordStateFlow = MutableStateFlow("")
    val passwordStateFlow = _passwordStateFlow.asStateFlow()


    fun setEmail(email: String) {
        _emailStateFlow.value = email
    }

    fun setPassword(password: String) {
        _passwordStateFlow.value = password
    }


    suspend fun fetchGoogleAuthInfo(authCode: String) = withContext(Dispatchers.IO) {
        getGoogleTokensUseCase(authCode)
    }

    suspend fun signUp(request: SignUpEmailRequest) = withContext(Dispatchers.IO) {
        signUpWithEmailUseCase(request)
    }

    suspend fun logInWithEmail(request: LogInEmailRequest) = withContext(Dispatchers.IO) {
        logInWithEmailUseCase(request)
    }
}