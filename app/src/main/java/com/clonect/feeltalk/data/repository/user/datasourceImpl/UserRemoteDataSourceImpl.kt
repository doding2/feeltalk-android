package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.user.UserInfo
import retrofit2.Response

class UserRemoteDataSourceImpl(
    private val clonectService: ClonectService,
): UserRemoteDataSource {

    override suspend fun signInWithGoogle(idToken: String): Response<String> {
        TODO()
    }

    override suspend fun getUserInfo(accessToken: String): Response<UserInfo> {
        return clonectService.getUserInfo(accessToken)
    }

}