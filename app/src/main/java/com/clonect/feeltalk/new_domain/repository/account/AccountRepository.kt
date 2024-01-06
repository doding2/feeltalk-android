package com.clonect.feeltalk.new_domain.repository.account

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.account.ConfigurationInfo
import com.clonect.feeltalk.new_domain.model.account.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.account.LockQA
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.model.account.RequestAdultAuthCodeDto
import com.clonect.feeltalk.new_domain.model.account.ServiceDataCountDto
import com.clonect.feeltalk.new_domain.model.account.SocialType
import com.clonect.feeltalk.new_domain.model.account.UnlockPartnerPasswordResponse
import com.clonect.feeltalk.new_domain.model.account.ValidateLockAnswerDto
import com.clonect.feeltalk.new_domain.model.newAccount.GetUserStatusNewResponse
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    suspend fun logInNew(oauthId: String, snsType: SocialType): Resource<TokenInfo>
    suspend fun logInApple(state: String): Resource<TokenInfo>
    suspend fun getUserStatusNew(accessToken: String): Resource<GetUserStatusNewResponse>
    suspend fun signUpNew(accessToken: String, nickname: String, marketingConsent: Boolean, fcmToken: String, appleState: String? = null): Resource<Unit>

    suspend fun requestAdultAuthCode(providerId: String, userName: String, userPhone: String, userBirthday: String, userGender: String, userNation: String): Resource<RequestAdultAuthCodeDto>
    suspend fun retryRequestAdultAuthCode(sessionUuid: String): Resource<Unit>
    suspend fun verifyAdultAuthCode(authNumber: String, sessionUuid: String): Resource<Unit>


    suspend fun autoLogIn(accessToken: String): Resource<AutoLogInDto>
    suspend fun reLogIn(socialToken: SocialToken): Resource<Pair<String, TokenInfo?>>
    suspend fun signUp(socialToken: SocialToken, isMarketingConsentAgreed: Boolean, nickname: String, fcmToken: String): Resource<TokenInfo>
    suspend fun logOut(accessToken: String): Resource<Unit>
    suspend fun deleteMyAccount(accessToken: String, category: String, etcReason: String?, deleteReason: String): Resource<Unit>

    suspend fun getCoupleCode(accessToken: String): Resource<CoupleCodeDto>
    suspend fun matchCouple(accessToken: String, coupleCode: String): Resource<Unit>
    suspend fun breakUpCouple(accessToken: String): Resource<Unit>

    suspend fun getMyInfo(accessToken: String): Resource<MyInfo>
    suspend fun getConfigurationInfo(accessToken: String): Resource<ConfigurationInfo>
    suspend fun submitSuggestion(accessToken: String, title: String?, body: String, email: String): Resource<Unit>
    suspend fun getServiceDataCount(accessToken: String): Resource<ServiceDataCountDto>

    suspend fun lockAccount(accessToken: String, password: String, qa: LockQA): Resource<Unit>
    suspend fun updateAccountLockPassword(accessToken: String, password: String): Resource<Unit>
    suspend fun matchPassword(accessToken: String, password: String): Resource<Boolean>
    suspend fun checkAccountLocked(accessToken: String): Resource<Boolean>
    suspend fun checkAccountLockedFlow(accessToken: String): Flow<Resource<Boolean>>
    suspend fun unlockAccount(accessToken: String): Resource<Unit>
    suspend fun getLockResetQuestion(accessToken: String): Resource<Int>
    suspend fun validateLockResetAnswer(accessToken: String, answer: String): Resource<ValidateLockAnswerDto>
    suspend fun unlockPartnerPassword(accessToken: String, chatIndex: Long): Resource<UnlockPartnerPasswordResponse>

    suspend fun setCoupleCreated(isCreated: Boolean)
    suspend fun getCoupleCreatedFlow(): Flow<Boolean>
}