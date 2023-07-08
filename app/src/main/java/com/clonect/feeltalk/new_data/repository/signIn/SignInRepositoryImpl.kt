package com.clonect.feeltalk.new_data.repository.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.plusSecondsBy
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInCacheDataSource
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInLocalDataSource
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInRemoteDataSource
import com.clonect.feeltalk.new_domain.model.signIn.AutoLogInDto
import com.clonect.feeltalk.new_domain.model.signIn.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import com.clonect.feeltalk.new_domain.repository.signIn.SignInRepository
import java.util.*
import kotlin.coroutines.cancellation.CancellationException

class SignInRepositoryImpl(
    private val cacheDataSource: SignInCacheDataSource,
    private val localDataSource: SignInLocalDataSource,
    private val remoteDataSource: SignInRemoteDataSource
): SignInRepository {

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
            val cache = cacheDataSource.getCoupleCode()
            if (cache != null) return Resource.Success(CoupleCodeDto(cache))

            val result = remoteDataSource.getCoupleCode(accessToken)
            cacheDataSource.saveCoupleCode(result.inviteCode)
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


}