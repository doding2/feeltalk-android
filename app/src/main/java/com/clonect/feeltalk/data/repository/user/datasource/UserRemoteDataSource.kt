package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.user.*
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun autoLogInWithGoogle(idToken: String): Response<AccessToken>
    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String): Response<AccessToken>
    suspend fun getUserInfo(accessToken: String): Response<UserInfo>
}