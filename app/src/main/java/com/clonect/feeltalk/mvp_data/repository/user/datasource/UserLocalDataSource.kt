package com.clonect.feeltalk.mvp_data.repository.user.datasource

import com.clonect.feeltalk.mvp_domain.model.data.user.UserInfo

interface UserLocalDataSource {

    suspend fun getAccessToken(): String?
    suspend fun saveAccessToken(accessToken: String)

    suspend fun getUserInfo(): UserInfo?
    suspend fun saveUserInfo(userInfo: UserInfo)

    suspend fun getUserProfileUrl(): String?
    suspend fun saveUserProfileUrl(url: String)

    suspend fun getCoupleRegistrationCode(): String?
    suspend fun saveCoupleRegistrationCode(code: String)
    suspend fun removeCoupleRegistrationCode()

    suspend fun getGoogleIdToken(): String?
    suspend fun saveGoogleIdToken(idToken: String)

    suspend fun getAppleLoggedIn(): Boolean?
    suspend fun saveIsAppleLoggedIn(isLoggedIn: Boolean)

    suspend fun clearCoupleInfo(): Boolean
    suspend fun clearAllExceptKeys(): Boolean
    suspend fun clearAll(): Boolean
}