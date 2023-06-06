package com.clonect.feeltalk.new_data.repository.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInCacheDataSource
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInLocalDataSource
import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInRemoteDataSource
import com.clonect.feeltalk.new_domain.model.signIn.CheckMemberTypeDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import com.clonect.feeltalk.new_domain.repository.signIn.SignInRepository
import kotlin.coroutines.cancellation.CancellationException

class SignInRepositoryImpl(
    private val cacheDataSource: SignInCacheDataSource,
    private val localDataSource: SignInLocalDataSource,
    private val remoteDataSource: SignInRemoteDataSource
): SignInRepository {

    override suspend fun checkMemberType(socialToken: SocialToken): Resource<CheckMemberTypeDto> {
        return try {
            val result = remoteDataSource.checkMemberType(socialToken)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun signUp(socialToken: SocialToken, nickname: String): Resource<TokenInfo> {
        return try {
            val result = remoteDataSource.signUp(socialToken, nickname)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


}