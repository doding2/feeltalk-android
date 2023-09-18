package com.clonect.feeltalk.new_domain.repository.account

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.account.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.account.LockQA
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo

interface AccountRepository {

    suspend fun autoLogIn(accessToken: String): Resource<AutoLogInDto>
    suspend fun reLogIn(socialToken: SocialToken): Resource<Pair<String, TokenInfo?>>
    suspend fun signUp(socialToken: SocialToken, isMarketingConsentAgreed: Boolean, nickname: String, fcmToken: String): Resource<TokenInfo>

    suspend fun getCoupleCode(accessToken: String): Resource<CoupleCodeDto>
    suspend fun matchCouple(accessToken: String, coupleCode: String): Resource<Unit>

    suspend fun lockAccount(accessToken: String, password: String, qa: LockQA): Resource<Unit>
    suspend fun updateAccountLockPassword(accessToken: String, password: String): Resource<Unit>
    suspend fun matchPassword(accessToken: String, password: String): Resource<Boolean>
    suspend fun checkAccountLocked(accessToken: String): Resource<Boolean>
    suspend fun getLockQA(accessToken: String): Resource<LockQA>
    suspend fun unlockAccount(accessToken: String): Resource<Unit>
}