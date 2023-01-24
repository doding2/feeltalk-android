package com.clonect.feeltalk.data.repository.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val cacheDataSource: UserCacheDataSource
): UserRepository {

    override suspend fun signInWithGoogle(idToken: String): Resource<String> {
        return signInWithGoogleFromServer(idToken)
    }

    override suspend fun getUserInfo(accessToken: String): Flow<Resource<UserInfo>> = flow {
        val cache = getUserInfoFromCache()
        emit(cache)

        val local = getUserInfoFromDB()
        if (local is Resource.Success) {
            cacheDataSource.saveUserInfoToCache(local.data)
        }
        emit(local)

        val remote = getUserInfoFromServer(accessToken)
        if (remote is Resource.Success) {
            localDataSource.saveUserInfoToDatabase(remote.data)
            cacheDataSource.saveUserInfoToCache(remote.data)
        }
        emit(remote)
    }


    private suspend fun signInWithGoogleFromServer(idToken: String): Resource<String> {
        return try {
            val response = remoteDataSource.signInWithGoogle(idToken)
            if (!response.isSuccessful) throw HttpException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")
            Resource.Success(response.body()!!)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e)
        }
    }

    private suspend fun getUserInfoFromCache(): Resource<UserInfo> {
        return try {
            val userInfo = cacheDataSource.getUserInfo()
                ?: throw NullPointerException("User info is not saved at cache yet.")
            Resource.Success(userInfo)
        } catch(e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e)
        }
    }

    private suspend fun getUserInfoFromDB(): Resource<UserInfo> {
        return try {
            val userInfo = localDataSource.getUserInfo()
                ?: throw NullPointerException("User info is not saved at database yet.")
            Resource.Success(userInfo)
        } catch(e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e)
        }
    }

    private suspend fun getUserInfoFromServer(accessToken: String): Resource<UserInfo> {
        return try {
            val response = remoteDataSource.getUserInfo(accessToken)
            if (!response.isSuccessful) throw HttpException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")
            Resource.Success(response.body()!!)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e)
        }
    }
}