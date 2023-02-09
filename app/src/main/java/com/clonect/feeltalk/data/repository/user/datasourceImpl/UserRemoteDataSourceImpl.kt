package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.dto.user.AccessTokenDto
import com.clonect.feeltalk.domain.model.dto.user.PartnerCodeCheckDto
import com.clonect.feeltalk.domain.model.dto.user.SignUpDto
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.user.CoupleCheckDto
import com.clonect.feeltalk.domain.model.dto.user.UserInfoDto
import com.google.gson.JsonObject
import retrofit2.HttpException
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class UserRemoteDataSourceImpl(
    private val clonectService: ClonectService,
): UserRemoteDataSource {

    override suspend fun getUserInfo(accessToken: String): Response<UserInfoDto> {
        val response = clonectService.getUserInfo(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun checkUserIsCouple(accessToken: String): Response<CoupleCheckDto> {
        val response =  clonectService.checkUserIsCouple(accessToken)
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

    override suspend fun updateUserInfo(
        accessToken: String,
        nickname: String,
        age: Long,
        birthDate: String,
        anniversary: String
    ): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("nickname", nickname)
            addProperty("age", age)
            addProperty("birth", birthDate)
            addProperty("coupleDay", anniversary)
        }
        val response = clonectService.updateUserInfo(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun updateMyEmotion(accessToken: String, emotion: String): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("emotion", emotion)
        }
        val response = clonectService.updateMyEmotion(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    override suspend fun sendPartnerCoupleRegistrationCode(
        accessToken: String,
        partnerCode: String,
    ): Response<PartnerCodeCheckDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("coupleCode", partnerCode)
        }
        val response = clonectService.sendPartnerCoupleRegistrationCode(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }



    override suspend fun autoLogInWithGoogle(idToken: String): Response<AccessTokenDto> {
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