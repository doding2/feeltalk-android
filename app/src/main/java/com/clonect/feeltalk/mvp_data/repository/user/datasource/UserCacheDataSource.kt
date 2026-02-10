package com.clonect.feeltalk.mvp_data.repository.user.datasource

import com.clonect.feeltalk.mvp_domain.model.data.user.UserInfo

interface UserCacheDataSource {
    fun getUserInfo(): UserInfo?
    fun saveUserInfoToCache(userInfo: UserInfo)

    fun getUserProfileUrl(): String?
    fun saveUserProfileUrl(url: String)

    fun getPartnerInfo(): UserInfo?
    fun savePartnerInfoToCache(profileInfo: UserInfo)

    fun getPartnerProfileUrl(): String?
    fun savePartnerProfileUrl(url: String)

    fun getAccessToken(): String?
    fun saveAccessTokenToCache(accessToken: String)

    fun getCoupleAnniversary(): String?
    fun saveCoupleAnniversary(date: String)

    fun getCoupleRegistrationCode(): String?
    fun saveCoupleRegistrationCode(code: String)
    fun clearCoupleRegistrationCode()
}