package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.data.user.UserInfo

interface UserLocalDataSource {

    suspend fun getAccessToken(): String?
    suspend fun saveAccessToken(accessToken: String)

    suspend fun getUserInfo(): UserInfo?
    suspend fun saveUserInfo(userInfo: UserInfo)

    suspend fun getCoupleAnniversary(): String?
    suspend fun saveCoupleAnniversary(date: String)

    suspend fun getCoupleRegistrationCode(): String?
    suspend fun saveCoupleRegistrationCode(code: String)
    suspend fun removeCoupleRegistrationCode()

    suspend fun getGoogleIdToken(): String?
    suspend fun saveGoogleIdToken(idToken: String)

    suspend fun clearCoupleInfo(): Boolean
    suspend fun clearAllExceptKeys(): Boolean
}