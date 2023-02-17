package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.user.AccessTokenDto
import com.clonect.feeltalk.domain.model.dto.user.CoupleCheckDto
import com.clonect.feeltalk.domain.model.dto.user.PartnerCodeCheckDto
import com.clonect.feeltalk.domain.model.dto.user.SignUpDto

interface UserRepository {

    suspend fun getAccessToken(): Resource<String>
    suspend fun getUserInfo(): Resource<UserInfo>
    suspend fun getPartnerInfo(): Resource<UserInfo>
    suspend fun getCoupleAnniversary(): Resource<String>

    suspend fun breakUpCouple(): Resource<StatusDto>
    suspend fun requestChangingPartnerEmotion(): Resource<StatusDto>

    suspend fun checkUserInfoIsEntered(): Resource<Boolean>
    suspend fun updateUserInfo(nickname: String, age: Long, birthDate: String, anniversary: String): Resource<StatusDto>
    suspend fun updateMyEmotion(emotion: String): Resource<StatusDto>

    suspend fun checkUserInCouple(): Resource<CoupleCheckDto>
    suspend fun getCoupleRegistrationCode(withCache: Boolean): Resource<String>
    suspend fun removeCoupleRegistrationCode()
    suspend fun sendPartnerCoupleRegistrationCode(partnerCode: String): Resource<PartnerCodeCheckDto>
    suspend fun updateFcmToken(fcmToken: String): Resource<StatusDto>

    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String, fcmToken: String): Resource<SignUpDto>
    suspend fun autoLogInWithGoogle(): Resource<AccessTokenDto>

    suspend fun signUpWithKakao(accessToken: String, fcmToken: String): Resource<SignUpDto>
    suspend fun autoLogInWithKakao(): Resource<AccessTokenDto>

    suspend fun signUpWithNaver(accessToken: String, fcmToken: String): Resource<SignUpDto>
    suspend fun autoLogInWithNaver(): Resource<AccessTokenDto>

    suspend fun clearCoupleInfo(): Resource<Boolean>
    suspend fun clearAllExceptKeys(): Resource<Boolean>
}