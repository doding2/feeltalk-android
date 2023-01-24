package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.*
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun signInWithGoogle(idToken: String): Resource<String>

    suspend fun getUserInfo(accessToken: String): Flow<Resource<UserInfo>>

}