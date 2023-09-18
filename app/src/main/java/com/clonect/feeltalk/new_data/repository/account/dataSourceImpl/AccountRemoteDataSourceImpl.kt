package com.clonect.feeltalk.new_data.repository.account.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.common.FeelTalkException.ServerIsDownException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountRemoteDataSource
import com.clonect.feeltalk.new_domain.model.account.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.account.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.account.LockQA
import com.clonect.feeltalk.new_domain.model.account.ReLogInDto
import com.clonect.feeltalk.new_domain.model.account.SignUpDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.user.SocialType
import com.google.gson.JsonObject

class AccountRemoteDataSourceImpl(
    private val clonectService: ClonectService
): AccountRemoteDataSource {

    override suspend fun autoLogIn(accessToken: String): AutoLogInDto {
        val response = clonectService.autoLogIn("Bearer $accessToken")
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun reLogIn(socialToken: SocialToken): ReLogInDto {
        val body = JsonObject().apply {
            addProperty("snsType", socialToken.type.raw)

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
        if (!response.isSuccessful) throw ServerIsDownException(response)
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
            addProperty("snsType", socialToken.type.raw)
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
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getCoupleCode(accessToken: String): CoupleCodeDto {
        val response = clonectService.getCoupleCode("Bearer $accessToken")
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun matchCouple(accessToken: String, coupleCode: String) {
        val body = JsonObject().apply {
            addProperty("inviteCode", coupleCode)
        }
        val response = clonectService.mathCouple("Bearer $accessToken", body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun lockAccount(
        accessToken: String,
        password: String,
        lockQA: LockQA
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAccountLockPassword(accessToken: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getLockQA(accessToken: String): LockQA {
        TODO("Not yet implemented")
    }

    override suspend fun checkAccountLock(accessToken: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unlockAccount(accessToken: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getLockPassword(accessToken: String): String {
        TODO("Not yet implemented")
    }


}