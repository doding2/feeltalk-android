package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.domain.model.data.user.UserInfo

class UserCacheDataSourceImpl: UserCacheDataSource {

    private var accessToken: String? = null
    private var userInfo: UserInfo? = null
    private var partnerInfo: UserInfo? = null
    private var coupleRegistrationCode: String? = null


    override fun getAccessToken() = accessToken

    override fun saveAccessTokenToCache(accessToken: String) {
        this.accessToken = accessToken
    }


    override fun getUserInfo() = userInfo

    override fun saveUserInfoToCache(userInfo: UserInfo) {
        this.userInfo = userInfo
    }

    override fun getPartnerInfo(): UserInfo? = partnerInfo

    override fun savePartnerInfoToCache(partnerInfo: UserInfo) {
        this.partnerInfo = partnerInfo
    }


    override fun getCoupleRegistrationCode(): String? = coupleRegistrationCode

    override fun saveCoupleRegistrationCode(code: String) {
        this.coupleRegistrationCode = code
    }

    override fun clearCoupleRegistrationCode() {
        coupleRegistrationCode = null
    }
}