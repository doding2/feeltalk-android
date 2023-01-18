package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.domain.model.user.GoogleTokens
import com.clonect.feeltalk.domain.model.user.UserInfo

class UserCacheDataSourceImpl: UserCacheDataSource {
    private var googleTokens: GoogleTokens? = null
    private var userInfo: UserInfo? = null

    override suspend fun getGoogleTokens(): GoogleTokens? {
        return googleTokens
    }

    override suspend fun getUserInfo(): UserInfo? {
        return userInfo
    }

    override suspend fun saveGoogleTokensToCache(tokens: GoogleTokens) {
        googleTokens = tokens
    }

    override suspend fun saveUserInfoToCache(userInfo: UserInfo) {
        this.userInfo = userInfo
    }
}