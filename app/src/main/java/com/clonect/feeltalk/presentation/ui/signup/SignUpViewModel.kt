package com.clonect.feeltalk.presentation.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.data.util.Resource
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailResponse
import com.clonect.feeltalk.domain.usecase.SignUpWithEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithEmail: SignUpWithEmail
): ViewModel() {

    suspend fun signUp(signUpEmailRequest: SignUpEmailRequest): Resource<SignUpEmailResponse> = withContext(viewModelScope.coroutineContext) {
        signUpWithEmail(signUpEmailRequest)
    }

}