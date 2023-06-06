package com.clonect.feeltalk.new_data.repository.signIn.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInRemoteDataSource
import com.clonect.feeltalk.new_domain.model.signIn.CheckMemberTypeDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import com.clonect.feeltalk.new_domain.model.user.SocialType
import com.google.gson.JsonObject
import retrofit2.HttpException

class SignInRemoteDataSourceImpl(
    private val clonectService: ClonectService
): SignInRemoteDataSource {

    override suspend fun checkMemberType(socialToken: SocialToken): CheckMemberTypeDto {
        val body = JsonObject().apply {
            addProperty("snsType", socialToken.type.toString().lowercase())

            when (socialToken.type) {
                SocialType.Kakao,
                SocialType.Naver -> {
                    addProperty("accessToken", socialToken.accessToken)
                    addProperty("refreshToken", socialToken.refreshToken)
                }
                SocialType.Google -> {
                    addProperty("idToken", socialToken.idToken)
                    addProperty("authCode", socialToken.serverAuthCode)
                }
                SocialType.Apple -> {
                    addProperty("code", socialToken.state)
                }
            }
        }
        val response = clonectService.checkMemberType(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "failure") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data
    }

    override suspend fun signUp(socialToken: SocialToken, nickname: String): TokenInfo {
        val body = JsonObject().apply {
            addProperty("nickName", nickname)
            addProperty("snsType", socialToken.type.toString().lowercase())

            when (socialToken.type) {
                SocialType.Kakao,
                SocialType.Naver -> {
                    addProperty("accessToken", socialToken.accessToken)
                    addProperty("refreshToken", socialToken.refreshToken)
                }
                SocialType.Google -> {
                    addProperty("idToken", socialToken.idToken)
                    addProperty("authCode", socialToken.serverAuthCode)
                }
                SocialType.Apple -> {
                    addProperty("code", socialToken.state)
                }
            }
        }
        val response = clonectService.signUp("Bearer ${socialToken.accessToken}", body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "failure") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data
    }


}