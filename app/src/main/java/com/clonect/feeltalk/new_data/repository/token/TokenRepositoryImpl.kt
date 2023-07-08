package com.clonect.feeltalk.new_data.repository.token

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.plusSecondsBy
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenCacheDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenLocalDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenRemoteDataSource
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository
import java.util.*
import kotlin.coroutines.cancellation.CancellationException

class TokenRepositoryImpl(
    private val cacheDataSource: TokenCacheDataSource,
    private val localDataSource: TokenLocalDataSource,
    private val remoteDataSource: TokenRemoteDataSource
): TokenRepository {

    override fun cacheSocialToken(socialToken: SocialToken): Resource<Unit> {
        cacheDataSource.saveSocialToken(socialToken)
        return Resource.Success(Unit)
    }
    override fun getCachedSocialToken(): Resource<SocialToken> {
        val socialToken = cacheDataSource.getSocialToken()
        return if (socialToken != null)
            Resource.Success(socialToken)
        else
            Resource.Error(NullPointerException("Social Token is not cached."))
    }

    override fun saveTokenInfo(tokenInfo: TokenInfo): Resource<Unit> {
        return try {
            cacheDataSource.saveTokenInfo(tokenInfo)
            localDataSource.saveTokenInfo(tokenInfo)
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getTokenInfo(): Resource<TokenInfo> {
        try {
            val cache = cacheDataSource.getTokenInfo()
            if (cache != null) return Resource.Success(renewToken(cache))


            val local = localDataSource.getTokenInfo()
            if (local != null) return Resource.Success(renewToken(local))

            return Resource.Error(NullPointerException("Tokens are not saved in local storage."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    private suspend fun renewToken(tokenInfo: TokenInfo): TokenInfo {
        val now = Date()
        if (tokenInfo.expiresAt <= now) {
            val renewResult = remoteDataSource.renewToken(tokenInfo)
            val newTokenInfo = TokenInfo(
                accessToken = renewResult.accessToken,
                refreshToken = renewResult.refreshToken,
                expiresAt = now.plusSecondsBy(renewResult.expiresIn),
                snsType = tokenInfo.snsType
            )
            cacheDataSource.saveTokenInfo(newTokenInfo)
            localDataSource.saveTokenInfo(newTokenInfo)
            return newTokenInfo
        }
        return tokenInfo
    }

}