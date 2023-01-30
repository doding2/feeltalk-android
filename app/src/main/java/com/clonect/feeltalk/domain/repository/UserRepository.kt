package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.*
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun autoLogInWithGoogle(idToken: String): Resource<AccessToken>

    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String): Resource<AccessToken>

    suspend fun getAccessToken(): Resource<AccessToken>

    suspend fun getUserInfo(accessToken: String): Flow<Resource<UserInfo>>

}