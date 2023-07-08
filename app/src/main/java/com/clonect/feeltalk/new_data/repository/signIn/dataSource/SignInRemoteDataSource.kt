package com.clonect.feeltalk.new_data.repository.signIn.dataSource

import com.clonect.feeltalk.new_domain.model.signIn.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.signIn.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.signIn.ReLogInDto
import com.clonect.feeltalk.new_domain.model.signIn.SignUpDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken

interface SignInRemoteDataSource {

    suspend fun autoLogIn(accessToken: String): AutoLogInDto
    suspend fun reLogIn(socialToken: SocialToken): ReLogInDto
    suspend fun signUp(socialToken: SocialToken, isMarketingConsentAgreed: Boolean, nickname: String, fcmToken: String): SignUpDto

    suspend fun getCoupleCode(accessToken: String): CoupleCodeDto
    suspend fun matchCouple(accessToken: String, coupleCode: String)


}