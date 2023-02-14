package com.clonect.feeltalk.data.repository.user.datasource

import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.user.*
import com.google.gson.JsonObject
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun getUserInfo(accessToken: String): Response<UserInfoDto>
    suspend fun getPartnerInfo(accessToken: String): Response<AccessTokenDto>
    suspend fun breakUpCouple(accessToken: String): Response<StatusDto>

    suspend fun getCoupleAnniversary(accessToken: String): Response<DDayDto>

    suspend fun checkUserIsCouple(accessToken: String): Response<CoupleCheckDto>
    suspend fun checkUserInfoIsEntered(accessToken: String): Response<JsonObject>

    suspend fun updateUserInfo(accessToken: String, nickname: String, age: Long, birthDate: String, anniversary: String): Response<StatusDto>
    suspend fun updateMyEmotion(accessToken: String, emotion: String): Response<StatusDto>

    suspend fun getCoupleRegistrationCode(accessToken: String): Response<CoupleRegistrationCodeDto>
    suspend fun sendPartnerCoupleRegistrationCode(accessToken: String, partnerCode: String): Response<PartnerCodeCheckDto>
    suspend fun updateFcmToken(accessToken: String, fcmToken: String): Response<StatusDto>

    suspend fun autoLogInWithGoogle(idToken: String): Response<AccessTokenDto>
    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String, fcmToken: String): Response<SignUpDto>

    suspend fun signUpWithKakao(idToken: String, fcmToken: String): Response<SignUpDto>
}