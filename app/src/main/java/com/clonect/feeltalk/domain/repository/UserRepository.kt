package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.*
import com.clonect.feeltalk.domain.model.user.dto.CoupleCheckDto
import com.clonect.feeltalk.domain.model.user.dto.SendPartnerCoupleRegistrationCodeDto

interface UserRepository {

    suspend fun getAccessToken(): Resource<AccessToken>
    suspend fun getUserInfo(): Resource<UserInfo>
    suspend fun checkUserInCouple(): Resource<CoupleCheckDto>

    suspend fun getCoupleRegistrationCode(): Resource<String>
    suspend fun removeCoupleRegistrationCode()
    suspend fun sendPartnerCoupleRegistrationCode(partnerCode: String): Resource<SendPartnerCoupleRegistrationCodeDto>

    suspend fun autoLogInWithGoogle(idToken: String): Resource<AccessToken>
    suspend fun signUpWithGoogle(idToken: String, serverAuthCode: String, fcmToken: String): Resource<AccessToken>

}