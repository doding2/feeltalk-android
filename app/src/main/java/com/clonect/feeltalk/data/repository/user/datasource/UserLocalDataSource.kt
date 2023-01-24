package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.user.UserInfo

interface UserLocalDataSource {
    suspend fun getUserInfo(): UserInfo?
    suspend fun saveUserInfoToDatabase(userInfo: UserInfo)
}