package com.clonect.feeltalk.mvp_data.repository.user.datasource

import android.graphics.Bitmap
import com.clonect.feeltalk.mvp_domain.model.dto.common.StatusDto
import com.clonect.feeltalk.mvp_domain.model.dto.user.*
import com.google.gson.JsonObject
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun getUserInfo(accessToken: String): Response<UserInfoDto>
    suspend fun getPartnerInfo(accessToken: String): Response<AccessTokenDto>
    suspend fun breakUpCouple(accessToken: String): Response<StatusDto>
    suspend fun requestChangingPartnerEmotion(accessToken: String): Response<StatusDto>

    suspend fun updateUserProfileImage(accessToken: String, image: Bitmap): Response<ProfileImageUrlDto>
    suspend fun getUserProfileUrl(accessToken: String): Response<ProfileImageUrlDto>

    suspend fun updateNickname(accessToken: String, nickname: String): Response<StatusDto>
    suspend fun updateBirth(accessToken: String, birth: String): Response<StatusDto>
    suspend fun updateCoupleAnniversary(accessToken: String, coupleAnniversary: String): Response<StatusDto>
    suspend fun getCoupleAnniversary(accessToken: String): Response<DDayDto>

    suspend fun checkUserIsCouple(accessToken: String): Response<CoupleCheckDto>
    suspend fun checkUserInfoIsEntered(accessToken: String): Response<JsonObject>

    suspend fun updateUserInfo(accessToken: String, gender: String, nickname: String, age: Long, birthDate: String, anniversary: String): Response<StatusDto>
    suspend fun updateMyEmotion(accessToken: String, emotion: String): Response<StatusDto>

    suspend fun getCoupleRegistrationCode(accessToken: String): Response<CoupleRegistrationCodeDto>
    suspend fun sendPartnerCoupleRegistrationCode(accessToken: String, partnerCode: String): Response<PartnerCodeCheckDto>
    suspend fun updateFcmToken(accessToken: String, fcmToken: String): Response<StatusDto>

    suspend fun autoLogIn(accessToken: String): Response<StatusDto>

    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String, fcmToken: String): Response<OldSignUpDto>
    suspend fun autoLogInWithGoogle(idToken: String): Response<AccessTokenDto>

    suspend fun signUpWithKakao(accessToken: String, fcmToken: String): Response<OldSignUpDto>
    suspend fun autoLogInWithKakao(accessToken: String): Response<AccessTokenDto>

    suspend fun signUpWithNaver(accessToken: String, fcmToken: String): Response<OldSignUpDto>
    suspend fun autoLogInWithNaver(accessToken: String): Response<AccessTokenDto>

    suspend fun signUpWithApple(accessToken: String, fcmToken: String): Response<OldSignUpDto>
    suspend fun autoLogInWithApple(accessToken: String): Response<AccessTokenDto>
    suspend fun getAppleAccessToken(uuid: String): Response<AccessTokenDto>

    suspend fun leaveFeeltalk(accessToken: String): Response<StatusCodeDto>
}