package com.clonect.feeltalk.new_data.repository.account.dataSource

import com.clonect.feeltalk.new_domain.model.account.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.account.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.account.LockQA
import com.clonect.feeltalk.new_domain.model.account.ReLogInDto
import com.clonect.feeltalk.new_domain.model.account.SignUpDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken

interface AccountRemoteDataSource {

    suspend fun autoLogIn(accessToken: String): AutoLogInDto
    suspend fun reLogIn(socialToken: SocialToken): ReLogInDto
    suspend fun signUp(socialToken: SocialToken, isMarketingConsentAgreed: Boolean, nickname: String, fcmToken: String): SignUpDto

    suspend fun getCoupleCode(accessToken: String): CoupleCodeDto
    suspend fun matchCouple(accessToken: String, coupleCode: String)

    suspend fun lockAccount(accessToken: String, password: String, lockQA: LockQA)
    suspend fun updateAccountLockPassword(accessToken: String, password: String)
    suspend fun getLockQA(accessToken: String): LockQA
    suspend fun getLockPassword(accessToken: String): String
    suspend fun checkAccountLock(accessToken: String): Boolean
    suspend fun unlockAccount(accessToken: String)
}