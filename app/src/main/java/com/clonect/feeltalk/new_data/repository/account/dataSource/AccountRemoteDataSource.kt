package com.clonect.feeltalk.new_data.repository.account.dataSource

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.account.ConfigurationInfoDto
import com.clonect.feeltalk.new_domain.model.account.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.account.GetPasswordDto
import com.clonect.feeltalk.new_domain.model.account.LockQA
import com.clonect.feeltalk.new_domain.model.account.LockResetQuestionDto
import com.clonect.feeltalk.new_domain.model.account.MyInfoDto
import com.clonect.feeltalk.new_domain.model.account.ReLogInDto
import com.clonect.feeltalk.new_domain.model.account.ServiceDataCountDto
import com.clonect.feeltalk.new_domain.model.account.SignUpDto
import com.clonect.feeltalk.new_domain.model.account.SocialType
import com.clonect.feeltalk.new_domain.model.account.ValidateLockAnswerDto
import com.clonect.feeltalk.new_domain.model.account.ValidatePasswordDto
import com.clonect.feeltalk.new_domain.model.newAccount.GetUserStatusNewResponse
import com.clonect.feeltalk.new_domain.model.newAccount.LogInNewResponse
import com.clonect.feeltalk.new_domain.model.newAccount.SignUpNewResponse
import com.clonect.feeltalk.new_domain.model.token.SocialToken

interface AccountRemoteDataSource {

    suspend fun logInNew(oauthId: String, snsType: SocialType): LogInNewResponse
    suspend fun getUserStatusNew(accessToken: String): GetUserStatusNewResponse
    suspend fun signUpNew(accessToken: String, nickname: String, marketingConsent: Boolean, fcmToken: String, appleState: String? = null)

    suspend fun autoLogIn(accessToken: String): AutoLogInDto
    suspend fun reLogIn(socialToken: SocialToken): ReLogInDto
    suspend fun signUp(socialToken: SocialToken, isMarketingConsentAgreed: Boolean, nickname: String, fcmToken: String): SignUpDto
    suspend fun logOut(accessToken: String)
    suspend fun deleteMyAccount(accessToken: String, category: String, etcReason: String?, deleteReason: String)

    suspend fun getCoupleCode(accessToken: String): CoupleCodeDto
    suspend fun matchCouple(accessToken: String, coupleCode: String)
    suspend fun breakUpCouple(accessToken: String)

    suspend fun getMyInfo(accessToken: String): MyInfoDto
    suspend fun getConfigurationInfo(accessToken: String): ConfigurationInfoDto
    suspend fun submitSuggestion(accessToken: String, title: String?, body: String, email: String)
    suspend fun getServiceDataCount(accessToken: String): ServiceDataCountDto

    suspend fun lockAccount(accessToken: String, password: String, lockQA: LockQA)
    suspend fun updateAccountLockPassword(accessToken: String, password: String)
    suspend fun validateLockPassword(accessToken: String, password: String): ValidatePasswordDto
    suspend fun getLockPassword(accessToken: String): GetPasswordDto
    suspend fun unlockAccount(accessToken: String)
    suspend fun getLockQuestion(accessToken: String): LockResetQuestionDto
    suspend fun validateLockAnswer(accessToken: String, answer: String): ValidateLockAnswerDto
}