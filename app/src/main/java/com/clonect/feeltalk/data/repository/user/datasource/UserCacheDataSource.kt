package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.user.AccessToken
import com.clonect.feeltalk.domain.model.user.UserInfo

interface UserCacheDataSource {
    fun getUserInfo(): UserInfo?
    fun saveUserInfoToCache(userInfo: UserInfo)

    fun getAccessToken(): AccessToken?
    fun saveAccessTokenToCache(accessToken: AccessToken)

    fun getCoupleRegistrationCode(): String?
    fun saveCoupleRegistrationCode(code: String)
    fun clearCoupleRegistrationCode()
}