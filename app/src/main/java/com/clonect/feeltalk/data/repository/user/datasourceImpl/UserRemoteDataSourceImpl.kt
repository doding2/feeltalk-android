package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.user.AccessToken
import com.clonect.feeltalk.domain.model.user.dto.SendPartnerCoupleRegistrationCodeDto
import com.clonect.feeltalk.domain.model.user.dto.SignUpDto
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.model.user.dto.CoupleCheckDto
import com.google.gson.JsonObject
import retrofit2.HttpException
import retrofit2.Response

class UserRemoteDataSourceImpl(
    private val clonectService: ClonectService,
): UserRemoteDataSource {

    override suspend fun getUserInfo(accessToken: AccessToken): Response<UserInfo> {
        val response = clonectService.getUserInfo(accessToken.value)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun checkUserIsCouple(accessToken: AccessToken): Response<CoupleCheckDto> {
        val response =  clonectService.checkUserIsCouple(accessToken.value)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun checkUserInfoIsEntered(accessToken: String): Response<JsonObject> {
        val response = clonectService.checkUserInfoIsEntered(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    // TODO 프로퍼티들 절대 저 이름들이 아님
    override suspend fun updateUserInfo(
        accessToken: String,
        nickname: String,
        birthDate: String,
        anniversary: String
    ): Response<String> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("nickname", nickname)
            addProperty("birthDate", birthDate)
            addProperty("anniversary", anniversary)
        }
        val response = clonectService.updateUserInfo(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    override suspend fun sendPartnerCoupleRegistrationCode(
        accessToken: AccessToken,
        partnerCode: String,
    ): Response<SendPartnerCoupleRegistrationCodeDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken.value)
            addProperty("coupleCode", partnerCode)
        }
        val response = clonectService.sendPartnerCoupleRegistrationCode(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }



    override suspend fun autoLogInWithGoogle(idToken: String): Response<AccessToken> {
        val body = JsonObject().apply {
            addProperty("idToken", idToken)
        }
        val response = clonectService.autoLogInWithGoogle(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun signUpWithGoogle(
        idToken: String,
        serverAuthCode: String,
        fcmToken: String
    ): Response<SignUpDto> {
        val body = JsonObject().apply {
            addProperty("idToken", idToken)
            addProperty("authCode", serverAuthCode)
            addProperty("fcmToken", fcmToken)
        }
        val response = clonectService.signUpWithGoogle(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

}