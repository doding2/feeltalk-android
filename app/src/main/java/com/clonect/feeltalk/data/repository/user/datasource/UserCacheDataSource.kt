package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.user.UserInfo

interface UserCacheDataSource {
    fun getUserInfo(): UserInfo?
    fun saveUserInfoToCache(userInfo: UserInfo)
}