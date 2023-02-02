package com.clonect.feeltalk.data.repository.user.datasource

interface UserLocalDataSource {

    suspend fun getCoupleRegistrationCode(): String?
    suspend fun saveCoupleRegistrationCode(code: String)
    suspend fun removeCoupleRegistrationCode()

}