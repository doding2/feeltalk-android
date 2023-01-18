package com.clonect.feeltalk.data.repository.user

import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.*
import com.clonect.feeltalk.domain.repository.UserRepository
import java.util.concurrent.CancellationException

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val cacheDataSource: UserCacheDataSource
): UserRepository {
    override suspend fun getGoogleTokens(authCode: String): Resource<GoogleTokens> {
        return getGoogleTokensFromCache(authCode)
    }

    override suspend fun signUpWithEmail(request: SignUpEmailRequest): Resource<UserInfo> {
        return signUpWithEmailFromServer(request)
    }

    override suspend fun logInWithEmail(request: LogInEmailRequest): Resource<UserInfo> {
        return getUserInfoFromCache(request)
    }

    override suspend fun getUserInfo(): Resource<UserInfo> {
        return getUserInfoFromCache()
    }


    private suspend fun signUpWithEmailFromServer(request: SignUpEmailRequest): Resource<UserInfo> {
        try {
            val response = remoteDataSource.signUpWithEmail(request)
            response.body()?.let {
                cacheDataSource.saveUserInfoToCache(it)
                return Resource.Success(it)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Resource.Error(e)
        }

        return Resource.Error(NullPointerException("The response body from the server is null."))
    }


    private suspend fun getGoogleTokensFromCache(authCode: String): Resource<GoogleTokens> {
        try {
            val googleTokens = cacheDataSource.getGoogleTokens()
            googleTokens?.let { return Resource.Success(it) }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Resource.Error(e)
        }

        return getGoogleTokensFromServer(authCode)
            .also {
                if (it is Resource.Success)
                    cacheDataSource.saveGoogleTokensToCache(it.data)
            }
    }

    private suspend fun getGoogleTokensFromServer(authCode: String): Resource<GoogleTokens> {
        try {
            GoogleTokenRequest(
                grant_type = "authorization_code",
                client_id = BuildConfig.GOOGLE_AUTH_CLIENT_ID,
                client_secret = BuildConfig.GOOGLE_AUTH_CLIENT_SECRET,
                redirect_uri = "",
                code = authCode
            ).also { request ->
                val response = remoteDataSource.getGoogleTokens(request)
                response.body()?.let { return Resource.Success(it) }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Resource.Error(e)
        }

        return Resource.Error(NullPointerException("The response body from the server is null."))
    }


    private suspend fun getUserInfoFromCache(request: LogInEmailRequest? = null): Resource<UserInfo> {
        try {
            val userInfo = cacheDataSource.getUserInfo()
            userInfo?.let { return Resource.Success(it) }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Resource.Error(e)
        }

        return request?.let {
            getUserInfoFromServer(request)
                .also {
                    if (it is Resource.Success)
                        cacheDataSource.saveUserInfoToCache(it.data)
                }
        } ?: Resource.Error(NullPointerException("Cache is not saved."))
    }

    private suspend fun getUserInfoFromServer(request: LogInEmailRequest): Resource<UserInfo> {
        try {
            val response = remoteDataSource.logInWithEmail(request)
            response.body()?.let { return Resource.Success(it) }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Resource.Error(e)
        }

        return Resource.Error(NullPointerException("The response body from the server is null."))
    }
}