package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.user.*
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun signInWithGoogle(idToken: String): Response<String>
    suspend fun getUserInfo(accessToken: String): Response<UserInfo>
}