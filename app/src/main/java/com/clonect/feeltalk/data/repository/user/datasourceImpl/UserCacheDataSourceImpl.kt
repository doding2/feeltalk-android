package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.domain.model.user.AccessToken
import com.clonect.feeltalk.domain.model.user.UserInfo

class UserCacheDataSourceImpl: UserCacheDataSource {

    private var accessToken: AccessToken? = null
    private var userInfo: UserInfo? = null


    override fun getAccessToken() = accessToken

    override fun saveAccessTokenToCache(accessToken: AccessToken) {
        this.accessToken = accessToken
    }

    override fun getUserInfo() = userInfo

    override fun saveUserInfoToCache(userInfo: UserInfo) {
        this.userInfo = userInfo
    }
}