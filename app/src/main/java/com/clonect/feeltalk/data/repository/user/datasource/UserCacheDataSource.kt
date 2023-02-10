package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.data.user.UserInfo

interface UserCacheDataSource {
    fun getUserInfo(): UserInfo?
    fun saveUserInfoToCache(userInfo: UserInfo)

    fun getPartnerInfo(): UserInfo?
    fun savePartnerInfoToCache(userInfo: UserInfo)

    fun getAccessToken(): String?
    fun saveAccessTokenToCache(accessToken: String)

    fun getCoupleRegistrationCode(): String?
    fun saveCoupleRegistrationCode(code: String)
    fun clearCoupleRegistrationCode()
}