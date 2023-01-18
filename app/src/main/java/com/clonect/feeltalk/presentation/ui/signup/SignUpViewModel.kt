package com.clonect.feeltalk.presentation.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.usecase.SignUpWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase
): ViewModel() {

    suspend fun signUp(request: SignUpEmailRequest): Resource<UserInfo> = withContext(viewModelScope.coroutineContext) {
        signUpWithEmailUseCase(request)
    }

}