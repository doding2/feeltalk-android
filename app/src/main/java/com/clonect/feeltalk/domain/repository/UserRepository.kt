package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.*

interface UserRepository {

    suspend fun getGoogleTokens(authCode: String): Resource<GoogleTokens>

    suspend fun signUpWithEmail(request: SignUpEmailRequest): Resource<UserInfo>

    suspend fun logInWithEmail(request: LogInEmailRequest): Resource<UserInfo>

    suspend fun getUserInfo(): Resource<UserInfo>

}