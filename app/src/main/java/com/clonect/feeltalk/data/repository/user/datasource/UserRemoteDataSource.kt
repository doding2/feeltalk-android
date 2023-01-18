package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.user.*
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun getGoogleTokens(request: GoogleTokenRequest): Response<GoogleTokens>
    suspend fun signUpWithEmail(request: SignUpEmailRequest): Response<UserInfo>
    suspend fun logInWithEmail(request: LogInEmailRequest): Response<UserInfo>
}