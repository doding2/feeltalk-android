package com.clonect.feeltalk.new_domain.repository.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.signIn.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.signIn.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo

interface SignInRepository {

    suspend fun autoLogIn(accessToken: String): Resource<AutoLogInDto>
    suspend fun reLogIn(socialToken: SocialToken): Resource<Pair<String, TokenInfo?>>
    suspend fun signUp(socialToken: SocialToken, isMarketingConsentAgreed: Boolean, nickname: String, fcmToken: String): Resource<TokenInfo>

    suspend fun getCoupleCode(accessToken: String): Resource<CoupleCodeDto>
    suspend fun matchCouple(accessToken: String, coupleCode: String): Resource<Unit>

}