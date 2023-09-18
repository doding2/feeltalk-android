package com.clonect.feeltalk.new_data.repository.account

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.plusSecondsBy
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountCacheDataSource
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountLocalDataSource
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountRemoteDataSource
import com.clonect.feeltalk.new_domain.model.account.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.account.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.account.LockQA
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import java.util.Date
import kotlin.coroutines.cancellation.CancellationException

class AccountRepositoryImpl(
    private val cacheDataSource: AccountCacheDataSource,
    private val localDataSource: AccountLocalDataSource,
    private val remoteDataSource: AccountRemoteDataSource
): AccountRepository {

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
        return try {
            val now = Date()
            val result = remoteDataSource.reLogIn(socialToken)
            val tokenInfo = if (result.signUpState == "newbie") {
                null
            } else {
                TokenInfo(
                    accessToken = result.accessToken ?: "",
                    refreshToken = result.refreshToken ?: "",
                    expiresAt = now.plusSecondsBy(result.expiresIn ?: 0),
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
            val tokenInfo = TokenInfo(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                expiresAt = now.plusSecondsBy(result.expiresIn),
                snsType = socialToken.type
            )
            Resource.Success(tokenInfo)
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


    override suspend fun lockAccount(
        accessToken: String,
        password: String,
        lockQA: LockQA
    ): Resource<Unit> {
        return try {
//            val result = remoteDataSource.lockAccount(accessToken, password, lockQA)
            localDataSource.saveLockPassword(password)
            localDataSource.saveLockQA(lockQA)
            Resource.Success(Unit)
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
//            val result = remoteDataSource.updateAccountLockPassword(accessToken, password)
            localDataSource.saveLockPassword(password)
            Resource.Success(Unit)
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
//            val remotePassword = remoteDataSource.updateAccountLockPassword(accessToken, password)
//            if (remotePassword != null) {
//                return Resource.Success(password == remotePassword)
//            }
            throw NullPointerException("Lock password from server is NULL")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun checkAccountLocked(accessToken: String): Resource<Boolean> {
        return try {
//            runCatching {
//                val remote = remoteDataSource.checkAccountLock(accessToken)
//                if (remote) {
//                    val lockPassword = remoteDataSource.getLockPassword(accessToken)
//                    localDataSource.saveLockPassword(lockPassword)
//                    Resource.Success(true)
//                } else {
//                    localDataSource.deleteLockInfo()
//                    Resource.Success(false)
//                }
//            }.onFailure {
//                val local = localDataSource.checkLockPassword()
//                Resource.Success(local)
//            }.onSuccess {
//                it
//            }

            val local = localDataSource.checkLockPassword()
            Resource.Success(local)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getLockQA(accessToken: String): Resource<LockQA> {
        return try {
            val local = localDataSource.getLockQA()
            if (local != null) {
                return Resource.Success(local)
            }
//            val remote = remoteDataSource.getLockQA(accessToken)
//            localDataSource.saveLockQA(remote)
//            return Resource.Success(remote)
            throw NullPointerException("Lock Question And Answer file does not exist.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun unlockAccount(accessToken: String): Resource<Unit> {
        return try {
//            val result = remoteDataSource.unlockAccount(accessToken)
            localDataSource.deleteLockInfo()
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


}