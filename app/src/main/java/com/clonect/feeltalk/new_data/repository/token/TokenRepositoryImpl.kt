package com.clonect.feeltalk.new_data.repository.token

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenCacheDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenLocalDataSource
import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenRemoteDataSource
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository
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
    override fun getTokenInfo(): Resource<TokenInfo> {
        try {
            val cache = cacheDataSource.getTokenInfo()
            if (cache != null) return Resource.Success(cache)

            val local = localDataSource.getTokenInfo()
            if (local != null) return Resource.Success(local)

            return Resource.Error(NullPointerException("Tokens are not saved in local storage."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

}