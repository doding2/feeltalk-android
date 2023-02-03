package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.user.AccessToken

interface UserLocalDataSource {

    suspend fun getAccessToken(): AccessToken?
    suspend fun saveAccessToken(accessToken: AccessToken)

    suspend fun getCoupleRegistrationCode(): String?
    suspend fun saveCoupleRegistrationCode(code: String)
    suspend fun removeCoupleRegistrationCode()

}