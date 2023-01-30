package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.user.AccessToken
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.google.gson.JsonObject
import retrofit2.Response

class UserRemoteDataSourceImpl(
    private val clonectService: ClonectService,
): UserRemoteDataSource {

    override suspend fun autoLogInWithGoogle(idToken: String): Response<AccessToken> {
        val obj = JsonObject()
        obj.addProperty("idToken", idToken)
        return clonectService.autoLogInWithGoogle(obj)
    }

    override suspend fun signUpWithGoogle(
        idToken: String,
        serverAuthCode: String,
    ): Response<AccessToken> {
        val obj = JsonObject().apply {
            addProperty("idToken", idToken)
            addProperty("authCode", serverAuthCode)
        }
        return clonectService.signUpWithGoogle(obj)
    }

    override suspend fun getUserInfo(accessToken: String): Response<UserInfo> {
        return clonectService.getUserInfo(accessToken)
    }

}