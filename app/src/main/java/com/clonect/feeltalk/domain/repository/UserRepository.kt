package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.domain.model.user.LogInGoogleResponse
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailResponse
import com.clonect.feeltalk.data.util.Resource

interface UserRepository {

    suspend fun fetchGoogleAuthInfo(authCode: String): Resource<LogInGoogleResponse>

    suspend fun signUpWithEmail(signUpEmailRequest: SignUpEmailRequest): Resource<SignUpEmailResponse>

}