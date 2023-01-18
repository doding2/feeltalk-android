package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.user.GoogleTokens
import com.clonect.feeltalk.domain.model.user.UserInfo

interface UserCacheDataSource {
    suspend fun getGoogleTokens(): GoogleTokens?
    suspend fun getUserInfo(): UserInfo?

    suspend fun saveGoogleTokensToCache(tokens: GoogleTokens)
    suspend fun saveUserInfoToCache(userInfo: UserInfo)
}