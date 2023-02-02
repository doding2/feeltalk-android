package com.clonect.feeltalk.data.repository.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.user.AccessToken
import com.clonect.feeltalk.domain.model.user.dto.CoupleCheckDto
import com.clonect.feeltalk.domain.model.user.dto.SendPartnerCoupleRegistrationCodeDto
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val cacheDataSource: UserCacheDataSource
): UserRepository {

    override suspend fun getAccessToken(): Resource<AccessToken> {
        return cacheDataSource.getAccessToken()
            ?.let {
                Resource.Success(it)
            } ?: Resource.Error(NullPointerException("User is not logged in."))
    }

    override suspend fun getUserInfo(): Resource<UserInfo> {
        val accessToken = cacheDataSource.getAccessToken()
            ?: throw NullPointerException("User is Not logged in.")

        val cache = getUserInfoFromCache()
        if (cache is Resource.Success) {
            return cache
        }

        val local = getUserInfoFromDB()
        if (local is Resource.Success) {
            cacheDataSource.saveUserInfoToCache(local.data)
            return local
        }

        val remote = getUserInfoFromServer(accessToken)
        if (remote is Resource.Success) {
//            localDataSource.saveUserInfoToDatabase(remote.data)
            cacheDataSource.saveUserInfoToCache(remote.data)
        }
        return remote
    }

    override suspend fun checkUserInCouple(): Resource<CoupleCheckDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.checkUserIsCouple(accessToken)
            if (!response.isSuccessful)
                throw HttpException(response)
            if (response.body() == null)
                throw NullPointerException("Response body from server is null.")

            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun getCoupleRegistrationCode(): Resource<String> {
        try {
            val cache = cacheDataSource.getCoupleRegistrationCode()
            if (cache != null)
                return Resource.Success(cache)

            val local = localDataSource.getCoupleRegistrationCode()
            if (local != null)
                return Resource.Success(local)

            throw Exception("Couple Registration Code Not Found.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun removeCoupleRegistrationCode() {
        localDataSource.removeCoupleRegistrationCode()
        cacheDataSource.clearCoupleRegistrationCode()
    }

    override suspend fun sendPartnerCoupleRegistrationCode(partnerCode: String): Resource<SendPartnerCoupleRegistrationCodeDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.sendPartnerCoupleRegistrationCode(accessToken, partnerCode)
            if (!response.isSuccessful)
                throw HttpException(response)
            if (response.body() == null)
                throw NullPointerException("Response body from server is null.")

            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun autoLogInWithGoogle(idToken: String): Resource<AccessToken> {
        return try {
            val response = remoteDataSource.autoLogInWithGoogle(idToken)

            if (!response.isSuccessful)
                throw HttpException(response)
            if (response.body() == null)
                throw NullPointerException("Response body from server is null.")

            val accessToken = response.body()!!
            cacheDataSource.saveAccessTokenToCache(accessToken)
            Resource.Success(accessToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun signUpWithGoogle(
        idToken: String,
        serverAuthCode: String,
        fcmToken: String
    ): Resource<AccessToken> {
        return try {
            val response = remoteDataSource.signUpWithGoogle(idToken, serverAuthCode, fcmToken)

            if (!response.isSuccessful)
                throw HttpException(response)
            if (response.body() == null)
                throw NullPointerException("Response body from server is null.")

            val coupleRegistrationCode = response.body()!!.validCode
            localDataSource.saveCoupleRegistrationCode(coupleRegistrationCode)
            cacheDataSource.saveCoupleRegistrationCode(coupleRegistrationCode)

            val accessToken = AccessToken(response.body()!!.token)
            cacheDataSource.saveAccessTokenToCache(accessToken)
            Resource.Success(accessToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }





    private fun getUserInfoFromCache(): Resource<UserInfo> {
        return try {
            val userInfo = cacheDataSource.getUserInfo()
                ?: throw NullPointerException("User info is not saved at cache yet.")
            Resource.Success(userInfo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private suspend fun getUserInfoFromDB(): Resource<UserInfo> {
        return try {
//            val userInfo = localDataSource.getUserInfo()
//                ?: throw NullPointerException("User info is not saved at database yet.")
//            Resource.Success(userInfo)
            Resource.Error(Exception("UserInfoLocalDataSource is not implemented"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private suspend fun getUserInfoFromServer(accessToken: AccessToken): Resource<UserInfo> {
        return try {
            val response = remoteDataSource.getUserInfo(accessToken)

            if (!response.isSuccessful)
                throw HttpException(response)
            if (response.body() == null)
                throw NullPointerException("Response body from server is null.")

            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}