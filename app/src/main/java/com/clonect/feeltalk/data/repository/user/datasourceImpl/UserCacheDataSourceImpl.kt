package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.domain.model.user.UserInfo

class UserCacheDataSourceImpl: UserCacheDataSource {

    private var userInfo: UserInfo? = null

    override suspend fun getUserInfo(): UserInfo? {
        return userInfo
    }

    override suspend fun saveUserInfoToCache(userInfo: UserInfo) {
        this.userInfo = userInfo
    }

}