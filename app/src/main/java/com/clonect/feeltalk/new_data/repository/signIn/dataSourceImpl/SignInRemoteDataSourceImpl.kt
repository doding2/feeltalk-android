package com.clonect.feeltalk.new_data.repository.signIn.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInRemoteDataSource
import com.clonect.feeltalk.new_domain.model.signIn.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.signIn.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.signIn.ReLogInDto
import com.clonect.feeltalk.new_domain.model.signIn.SignUpDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.user.SocialType
import com.google.gson.JsonObject
import retrofit2.HttpException

class SignInRemoteDataSourceImpl(
    private val clonectService: ClonectService
): SignInRemoteDataSource {

    override suspend fun autoLogIn(accessToken: String): AutoLogInDto {
        val response = clonectService.autoLogIn("Bearer $accessToken")
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun reLogIn(socialToken: SocialToken): ReLogInDto {
        val body = JsonObject().apply {
            addProperty("snsType", socialToken.type.toString().lowercase())

            when (socialToken.type) {
                SocialType.Kakao,
                SocialType.Naver -> {
                    addProperty("refreshToken", socialToken.refreshToken)
                }
                SocialType.Google -> {
                    addProperty("idToken", socialToken.idToken)
                    addProperty("authCode", socialToken.serverAuthCode)
                }
                SocialType.Apple -> {
                    addProperty("state", socialToken.state)
                }
            }
        }
        val response = clonectService.reLogIn(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun signUp(
        socialToken: SocialToken,
        isMarketingConsentAgreed: Boolean,
        nickname: String,
        fcmToken: String
    ): SignUpDto {
        val body = JsonObject().apply {
            addProperty("marketingConsent", true)
            addProperty("nickname", nickname)
            addProperty("snsType", socialToken.type.toString().lowercase())
            addProperty("fcmToken", fcmToken)

            when (socialToken.type) {
                SocialType.Kakao,
                SocialType.Naver -> {
                    addProperty("refreshToken", socialToken.refreshToken)
                }
                SocialType.Google -> {
                    addProperty("idToken", socialToken.idToken)
                    addProperty("authCode", socialToken.serverAuthCode)
                }
                SocialType.Apple -> {
                    addProperty("state", socialToken.state)
                }
            }
        }
        val response = clonectService.signUp(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getCoupleCode(accessToken: String): CoupleCodeDto {
        val response = clonectService.getCoupleCode("Bearer $accessToken")
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun matchCouple(accessToken: String, coupleCode: String) {
        val body = JsonObject().apply {
            addProperty("inviteCode", coupleCode)
        }
        val response = clonectService.mathCouple("Bearer $accessToken", body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }


}