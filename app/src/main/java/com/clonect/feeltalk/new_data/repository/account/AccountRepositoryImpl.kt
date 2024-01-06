package com.clonect.feeltalk.new_data.repository.account

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.plusHoursBy
import com.clonect.feeltalk.common.plusSecondsBy
import com.clonect.feeltalk.new_data.mapper.toConfigurationInfo
import com.clonect.feeltalk.new_data.mapper.toMyInfo
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountCacheDataSource
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountLocalDataSource
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountRemoteDataSource
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
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException


class AccountRepositoryImpl(
    private val cacheDataSource: AccountCacheDataSource,
    private val localDataSource: AccountLocalDataSource,
    private val remoteDataSource: AccountRemoteDataSource
): AccountRepository {
    override suspend fun logInNew(oauthId: String, snsType: SocialType): Resource<TokenInfo> {
        return try {
            val now = Date()
            val result = remoteDataSource.logInNew(oauthId, snsType)
            localDataSource.clearInternalStorage()
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())
            val tokenInfo = TokenInfo(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                accessExpiresAt = now.plusHoursBy(1),
                refreshExpiresAt = formatter.parse(result.expiredTime)
                    ?: throw IllegalStateException("Token expired time is invalid"),
                snsType = snsType
            )
            Resource.Success(tokenInfo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun logInApple(state: String): Resource<TokenInfo> {
        return try {
            val result = remoteDataSource.logInApple(state)
            localDataSource.clearInternalStorage()
            val now = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())
            val tokenInfo = TokenInfo(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                accessExpiresAt = now.plusHoursBy(1),
                refreshExpiresAt = formatter.parse(result.expiredTime)
                    ?: throw IllegalStateException("Token expired time is invalid"),
                snsType = SocialType.AppleAndroid
            )
            Resource.Success(tokenInfo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getUserStatusNew(accessToken: String): Resource<GetUserStatusNewResponse> {
        return try {
            val result = remoteDataSource.getUserStatusNew(accessToken)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun signUpNew(
        accessToken: String,
        nickname: String,
        marketingConsent: Boolean,
        fcmToken: String,
        appleState: String?,
    ): Resource<Unit> {
        return try {
            val result = remoteDataSource.signUpNew(accessToken, nickname, marketingConsent, fcmToken, appleState)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun requestAdultAuthCode(
        providerId: String,
        userName: String,
        userPhone: String,
        userBirthday: String,
        userGender: String,
        userNation: String,
    ): Resource<RequestAdultAuthCodeDto> {
        return try {
            val result = remoteDataSource.requestAdultAuthCode(providerId, userName, userPhone, userBirthday, userGender, userNation)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun retryRequestAdultAuthCode(
        sessionUuid: String
    ): Resource<Unit> {
        return try {
            val result = remoteDataSource.retryRequestAdultAuthCode(sessionUuid)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun verifyAdultAuthCode(authNumber: String, sessionUuid: String): Resource<Unit> {
        return try {
            val result = remoteDataSource.verifyAdultAuthCode(authNumber, sessionUuid)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun autoLogIn(accessToken: String): Resource<AutoLogInDto> {
        return try {
            val result = remoteDataSource.autoLogIn(accessToken)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun reLogIn(socialToken: SocialToken): Resource<Pair<String, TokenInfo?>> {
        val now = Date()
        return try {
            val result = remoteDataSource.reLogIn(socialToken)
            localDataSource.clearInternalStorage()
            val tokenInfo = if (result.signUpState == "newbie") {
                null
            } else {
                TokenInfo(
                    accessToken = result.accessToken ?: "",
                    refreshToken = result.refreshToken ?: "",
                    accessExpiresAt = now.plusHoursBy(1),
                    refreshExpiresAt = now.plusSecondsBy(result.expiresIn ?: 0),
                    snsType = socialToken.type
                )
            }
            Resource.Success(Pair(result.signUpState, tokenInfo))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun signUp(socialToken: SocialToken, isMarketingConsentAgreed: Boolean, nickname: String, fcmToken: String): Resource<TokenInfo> {
        return try {
            val now = Date()
            val result = remoteDataSource.signUp(socialToken, isMarketingConsentAgreed, nickname, fcmToken)
            localDataSource.clearInternalStorage()
            val tokenInfo = TokenInfo(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                accessExpiresAt = now.plusHoursBy(1),
                refreshExpiresAt = now.plusSecondsBy(result.expiresIn),
                snsType = socialToken.type
            )
            Resource.Success(tokenInfo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun logOut(accessToken: String): Resource<Unit> {
        return try {
            remoteDataSource.logOut(accessToken)
            localDataSource.clearInternalStorage()
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteMyAccount(
        accessToken: String,
        category: String,
        etcReason: String?,
        deleteReason: String,
    ): Resource<Unit> {
        return try {
            remoteDataSource.deleteMyAccount(accessToken, category, etcReason, deleteReason)
            localDataSource.clearInternalStorage()
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getCoupleCode(accessToken: String): Resource<CoupleCodeDto> {
        return try {
            val result = remoteDataSource.getCoupleCode(accessToken)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun matchCouple(accessToken: String, coupleCode: String): Resource<Unit> {
        return try {
            val result = remoteDataSource.matchCouple(accessToken, coupleCode)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun breakUpCouple(accessToken: String): Resource<Unit> {
        return try {
            val result = remoteDataSource.breakUpCouple(accessToken)
            localDataSource.clearInternalStorage()
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getMyInfo(accessToken: String): Resource<MyInfo> {
        try {
            val cache = cacheDataSource.getMyInfo()
            if (cache != null) return Resource.Success(cache)

            val local = localDataSource.getMyInfo()
            if (local != null) {
                cacheDataSource.saveMyInfo(local)
                return Resource.Success(local)
            }

            val remote = remoteDataSource.getMyInfo(accessToken).toMyInfo()
            cacheDataSource.saveMyInfo(remote)
            localDataSource.saveMyInfo(remote)
            return Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun getConfigurationInfo(accessToken: String): Resource<ConfigurationInfo> {
        try {
            val cache = cacheDataSource.getConfigurationInfo()
            if (cache != null) return Resource.Success(cache)

            val local = localDataSource.getConfigurationInfo()
            if (local != null) {
                cacheDataSource.saveConfigurationInfo(local)
                return Resource.Success(local)
            }

            val remote = remoteDataSource.getConfigurationInfo(accessToken).toConfigurationInfo()
            cacheDataSource.saveConfigurationInfo(remote)
            localDataSource.saveConfigurationInfo(remote)
            return Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun submitSuggestion(
        accessToken: String,
        title: String?,
        body: String,
        email: String,
    ): Resource<Unit> {
        return try {
            val result = remoteDataSource.submitSuggestion(accessToken, title, body, email)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getServiceDataCount(accessToken: String): Resource<ServiceDataCountDto> {
        return try {
            val cache = cacheDataSource.getServiceDataCount()
            if (cache != null) {
                return Resource.Success(cache)
            }
            val result = remoteDataSource.getServiceDataCount(accessToken)
            cacheDataSource.saveServiceDataCount(result)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun lockAccount(
        accessToken: String,
        password: String,
        lockQA: LockQA
    ): Resource<Unit> {
        return try {
            val result = remoteDataSource.lockAccount(accessToken, password, lockQA)
            localDataSource.saveLockPassword(password)
            localDataSource.saveLockQA(lockQA)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun updateAccountLockPassword(
        accessToken: String,
        password: String,
    ): Resource<Unit> {
        return try {
            val result = remoteDataSource.updateAccountLockPassword(accessToken, password)
            localDataSource.saveLockPassword(password)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun matchPassword(accessToken: String, password: String): Resource<Boolean> {
        try {
            val localPassword = localDataSource.getLockPassword()
            if (localPassword != null) {
                return Resource.Success(password == localPassword)
            }
            val remote = remoteDataSource.validateLockPassword(accessToken, password)
            return Resource.Success(remote.isValid)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun checkAccountLocked(accessToken: String): Resource<Boolean> {
        return try {
            runCatching {
                val remotePassword = remoteDataSource.getLockPassword(accessToken)
                val isAccountLocked = remotePassword.password != null
                if (isAccountLocked) {
                    localDataSource.saveLockPassword(remotePassword.password!!)
                    Resource.Success(true)
                } else {
                    localDataSource.deleteLockInfo()
                    Resource.Success(false)
                }
            }.onSuccess {
                Resource.Success(it.data)
            }.onFailure {
                val local = localDataSource.checkLockPassword()
                Resource.Success(local)
            }.getOrThrow()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun checkAccountLockedFlow(accessToken: String): Flow<Resource<Boolean>> {
        val localFlow = channelFlow {
            val local = localDataSource.checkLockPassword()
            send(Resource.Success(local))
        }
        val remoteFlow = channelFlow {
            try {
                val remotePassword = remoteDataSource.getLockPassword(accessToken)
                val isAccountLocked = remotePassword.password != null
                val result = if (isAccountLocked) {
                    localDataSource.saveLockPassword(remotePassword.password!!)
                    Resource.Success(true)
                } else {
                    localDataSource.deleteLockInfo()
                    Resource.Success(false)
                }
                send(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                send(Resource.Error(e))
            }
        }
        return flowOf(localFlow, remoteFlow).flattenMerge()
    }

    override suspend fun getLockResetQuestion(accessToken: String): Resource<Int> {
        return try {
            val local = localDataSource.getLockQA()
            if (local != null) {
                return Resource.Success(local.questionType)
            }
            val remote = remoteDataSource.getLockQuestion(accessToken)
            val questionType = when (remote.questionType) {
                "treasure" -> 0
                "celebrity" -> 1
                "date" -> 2
                "travel" -> 3
                "bucketList" -> 4
                else -> throw IllegalStateException("The question type from server is invalid.")
            }
            localDataSource.saveLockQA(
                LockQA(
                    questionType = questionType,
                    answer = null
                )
            )
            Resource.Success(questionType)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun validateLockResetAnswer(
        accessToken: String,
        answer: String,
    ): Resource<ValidateLockAnswerDto> {
        try {
            val local = localDataSource.getLockQA()
            if (local?.answer != null) {
                return Resource.Success(ValidateLockAnswerDto(local.answer == answer))
            }
            val remote = remoteDataSource.validateLockAnswer(accessToken, answer)
            return Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun unlockPartnerPassword(
        accessToken: String,
        chatIndex: Long,
    ): Resource<UnlockPartnerPasswordResponse> {
        return try {
            val remote = remoteDataSource.unlockPartnerPassword(accessToken, chatIndex)
            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun unlockAccount(accessToken: String): Resource<Unit> {
        return try {
            val result = remoteDataSource.unlockAccount(accessToken)
            localDataSource.deleteLockInfo()
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun setCoupleCreated(isCreated: Boolean) {
        cacheDataSource.setCoupleCreated(isCreated)
    }

    override suspend fun getCoupleCreatedFlow(): Flow<Boolean> {
        return cacheDataSource.getCoupleCreatedFlow()
    }
}