package com.clonect.feeltalk.release_data.repository.account.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.common.FeelTalkException.ServerIsDownException
import com.clonect.feeltalk.release_data.api.ClonectService
import com.clonect.feeltalk.release_data.repository.account.dataSource.AccountRemoteDataSource
import com.clonect.feeltalk.release_domain.model.account.AutoLogInDto
import com.clonect.feeltalk.release_domain.model.account.ConfigurationInfoDto
import com.clonect.feeltalk.release_domain.model.account.CoupleCodeDto
import com.clonect.feeltalk.release_domain.model.account.GetPasswordDto
import com.clonect.feeltalk.release_domain.model.account.LockQA
import com.clonect.feeltalk.release_domain.model.account.LockResetQuestionDto
import com.clonect.feeltalk.release_domain.model.account.MyInfoDto
import com.clonect.feeltalk.release_domain.model.account.ReLogInDto
import com.clonect.feeltalk.release_domain.model.account.RequestAdultAuthCodeDto
import com.clonect.feeltalk.release_domain.model.account.ServiceDataCountDto
import com.clonect.feeltalk.release_domain.model.account.SignUpDto
import com.clonect.feeltalk.release_domain.model.account.SocialType
import com.clonect.feeltalk.release_domain.model.account.UnlockPartnerPasswordResponse
import com.clonect.feeltalk.release_domain.model.account.ValidateLockAnswerDto
import com.clonect.feeltalk.release_domain.model.account.ValidatePasswordDto
import com.clonect.feeltalk.release_domain.model.newAccount.GetUserStatusNewResponse
import com.clonect.feeltalk.release_domain.model.newAccount.LogInNewResponse
import com.clonect.feeltalk.release_domain.model.token.SocialToken
import com.google.gson.JsonObject

class AccountRemoteDataSourceImpl(
    private val clonectService: ClonectService
): AccountRemoteDataSource {
    override suspend fun logInNew(oauthId: String, snsType: SocialType): LogInNewResponse {
        val body = JsonObject().apply {
            addProperty("oauthId", oauthId)
            addProperty("snsType", snsType.raw.uppercase())
        }
        val response = clonectService.logInNew(body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun logInApple(state: String): LogInNewResponse {
        val body = JsonObject().apply {
            addProperty("state", state)
        }
        val response = clonectService.logInApple(body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getUserStatusNew(accessToken: String): GetUserStatusNewResponse {
        val response = clonectService.getUserStatusNew(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun signUpNew(
        accessToken: String,
        nickname: String,
        marketingConsent: Boolean,
        fcmToken: String,
        appleState: String?,
    ) {
        val body = JsonObject().apply {
            addProperty("nickname", nickname)
            addProperty("marketingConsent", marketingConsent)
            addProperty("fcmToken", fcmToken)
            if (appleState != null) addProperty("appleState", appleState)
        }
        val response = clonectService.signUpNew(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
    }

    override suspend fun requestAdultAuthCode(
        providerId: String,
        userName: String,
        userPhone: String,
        userBirthday: String,
        userGender: String,
        userNation: String,
    ): RequestAdultAuthCodeDto {
        val body = JsonObject().apply {
            addProperty("providerId", providerId)
            addProperty("reqAuthType", "SMS")
            addProperty("userName", userName)
            addProperty("userPhone", userPhone)
            addProperty("userBirthday", userBirthday)
            addProperty("userGender", userGender)
            addProperty("userNation", userNation)
        }
        val response = clonectService.requestAdultAuthCode(body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun retryRequestAdultAuthCode(
        sessionUuid: String
    ) {
        val body = JsonObject().apply {
            addProperty("sessionUuid", sessionUuid)
        }
        val response = clonectService.retryRequestAdultAuthCode(body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun verifyAdultAuthCode(authNumber: String, sessionUuid: String) {
        val body = JsonObject().apply {
            addProperty("authNumber", authNumber)
            addProperty("sessionUuid", sessionUuid)
        }
        val response = clonectService.verifyAdultAuthCode(body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun autoLogIn(accessToken: String): AutoLogInDto {
        val response = clonectService.autoLogIn(accessToken)
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
                SocialType.Apple, SocialType.Apple -> {
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
                SocialType.Apple, SocialType.Apple -> {
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

    override suspend fun logOut(accessToken: String) {
        val response = clonectService.logOut(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun deleteMyAccount(
        accessToken: String,
        category: String,
        etcReason: String?,
        deleteReason: String,
    ) {
        val body = JsonObject().apply {
            addProperty("category", category)
            addProperty("etcReason", etcReason)
            addProperty("deleteReason", deleteReason)
        }
        val response = clonectService.deleteMyAccount(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun getCoupleCode(accessToken: String): CoupleCodeDto {
        val response = clonectService.getCoupleCode(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun matchCouple(accessToken: String, coupleCode: String) {
        val body = JsonObject().apply {
            addProperty("inviteCode", coupleCode)
        }
        val response = clonectService.matchCouple(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun breakUpCouple(accessToken: String) {
        val response = clonectService.breakUpCouple(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun getMyInfo(accessToken: String): MyInfoDto {
        val response = clonectService.getMyInfo(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getConfigurationInfo(accessToken: String): ConfigurationInfoDto {
        val response = clonectService.getConfigurationInfo(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun submitSuggestion(
        accessToken: String,
        title: String?,
        body: String,
        email: String,
    ) {
        val body = JsonObject().apply {
            addProperty("title", title)
            addProperty("body", body)
            addProperty("email", email)
        }
        val response = clonectService.submitSuggestion(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun getServiceDataCount(accessToken: String): ServiceDataCountDto {
        val response = clonectService.getServiceDataCount(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun lockAccount(
        accessToken: String,
        password: String,
        lockQA: LockQA
    ) {
        val body = JsonObject().apply {
            addProperty("password", password)
            addProperty("answer", lockQA.answer)
            addProperty("questionType", when (lockQA.questionType) {
                0 -> "treasure"
                1 -> "celebrity"
                2 -> "date"
                3 -> "travel"
                4 -> "bucketList"
                else -> throw IllegalArgumentException("Lock question type is invalid")
            })
        }
        val response = clonectService.setupPassword(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun updateAccountLockPassword(accessToken: String, password: String) {
        val body = JsonObject().apply {
            addProperty("password", password)
        }
        val response = clonectService.updatePassword(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun validateLockPassword(
        accessToken: String,
        password: String,
    ): ValidatePasswordDto {
        val body = JsonObject().apply {
            addProperty("password", password)
        }
        val response = clonectService.validatePassword(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getLockPassword(accessToken: String): GetPasswordDto {
        val response = clonectService.getPassword(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun unlockAccount(accessToken: String) {
        val response = clonectService.unlockPassword(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun getLockQuestion(accessToken: String): LockResetQuestionDto {
        val response = clonectService.getLockResetQuestion(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun validateLockAnswer(accessToken: String, answer: String): ValidateLockAnswerDto {
        val body = JsonObject().apply {
            addProperty("answer", answer)
        }
        val response = clonectService.validateLockResetAnswer(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun unlockPartnerPassword(
        accessToken: String,
        chatIndex: Long,
    ): UnlockPartnerPasswordResponse {
        val body = JsonObject().apply {
            addProperty("chattingMessageIndex", chatIndex)
        }
        val response = clonectService.unlockPartnerPassword(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

}