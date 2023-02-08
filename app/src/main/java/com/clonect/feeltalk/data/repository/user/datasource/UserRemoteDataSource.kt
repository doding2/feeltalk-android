package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.user.*
import com.clonect.feeltalk.domain.model.user.dto.CoupleCheckDto
import com.clonect.feeltalk.domain.model.user.dto.SendPartnerCoupleRegistrationCodeDto
import com.clonect.feeltalk.domain.model.user.dto.SignUpDto
import com.google.gson.JsonObject
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun getUserInfo(accessToken: AccessToken): Response<UserInfo>
    suspend fun checkUserIsCouple(accessToken: AccessToken): Response<CoupleCheckDto>
    suspend fun checkUserInfoIsEntered(accessToken: String): Response<JsonObject>

    suspend fun updateUserInfo(accessToken: String, nickname: String, birthDate: String, anniversary: String): Response<String>

    suspend fun sendPartnerCoupleRegistrationCode(accessToken: AccessToken, partnerCode: String): Response<SendPartnerCoupleRegistrationCodeDto>

    suspend fun autoLogInWithGoogle(idToken: String): Response<AccessToken>
    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String, fcmToken: String): Response<SignUpDto>
}