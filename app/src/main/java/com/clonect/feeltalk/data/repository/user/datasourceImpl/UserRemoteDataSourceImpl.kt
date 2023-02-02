package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.user.AccessToken
import com.clonect.feeltalk.domain.model.user.dto.SendPartnerCoupleRegistrationCodeDto
import com.clonect.feeltalk.domain.model.user.dto.SignUpDto
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.model.user.dto.CoupleCheckDto
import com.google.gson.JsonObject
import retrofit2.Response

class UserRemoteDataSourceImpl(
    private val clonectService: ClonectService,
): UserRemoteDataSource {

    override suspend fun getUserInfo(accessToken: AccessToken): Response<UserInfo> {
        return clonectService.getUserInfo(accessToken.value)
    }

    override suspend fun checkUserIsCouple(accessToken: AccessToken): Response<CoupleCheckDto> {
        return clonectService.checkUserIsCouple(accessToken.value)
    }

    override suspend fun sendPartnerCoupleRegistrationCode(
        accessToken: AccessToken,
        partnerCode: String,
    ): Response<SendPartnerCoupleRegistrationCodeDto> {
        val obj = JsonObject().apply {
            addProperty("accessToken", accessToken.value)
            addProperty("coupleCode", partnerCode)
        }
        return clonectService.sendPartnerCoupleRegistrationCode(obj)
    }

    override suspend fun autoLogInWithGoogle(idToken: String): Response<AccessToken> {
        val obj = JsonObject().apply {
            addProperty("idToken", idToken)
        }
        return clonectService.autoLogInWithGoogle(obj)
    }

    override suspend fun signUpWithGoogle(
        idToken: String,
        serverAuthCode: String,
        fcmToken: String
    ): Response<SignUpDto> {
        val obj = JsonObject().apply {
            addProperty("idToken", idToken)
            addProperty("authCode", serverAuthCode)
            addProperty("fcmToken", fcmToken)
        }
        return clonectService.signUpWithGoogle(obj)
    }

}