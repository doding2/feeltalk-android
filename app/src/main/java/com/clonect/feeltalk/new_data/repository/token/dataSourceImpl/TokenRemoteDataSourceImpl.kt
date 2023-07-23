package com.clonect.feeltalk.new_data.repository.token.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenRemoteDataSource
import com.clonect.feeltalk.new_domain.model.token.RenewTokenDto
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import com.google.gson.JsonObject
import retrofit2.HttpException

class TokenRemoteDataSourceImpl(
    private val clonectService: ClonectService
): TokenRemoteDataSource {

    override suspend fun updateFcmToken(accessToken: String, fcmToken: String) {
        val body = JsonObject().apply {
            addProperty("fcmToken", fcmToken)
        }
        val response = clonectService.updateFcmToken("Bearer $accessToken", body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun renewToken(tokenInfo: TokenInfo): RenewTokenDto {
        val body = JsonObject().apply {
            addProperty("refreshToken", tokenInfo.refreshToken)
        }
        val response = clonectService.renewToken(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

}