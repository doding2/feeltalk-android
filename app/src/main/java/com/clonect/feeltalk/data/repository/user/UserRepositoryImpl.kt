package com.clonect.feeltalk.data.repository.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.mapper.toEmotion
import com.clonect.feeltalk.data.mapper.toUserInfo
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.user.AccessTokenDto
import com.clonect.feeltalk.domain.model.dto.user.CoupleCheckDto
import com.clonect.feeltalk.domain.model.dto.user.PartnerCodeCheckDto
import com.clonect.feeltalk.domain.model.dto.user.SignUpDto
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.CancellationException

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val cacheDataSource: UserCacheDataSource
): UserRepository {

    override suspend fun getAccessToken(): Resource<String> {
        return cacheDataSource.getAccessToken()
            ?.let {
                Resource.Success(it)
            } ?: Resource.Error(NullPointerException("User is not logged in."))
    }

    override suspend fun getUserInfo(): Resource<UserInfo> {
        val accessToken = cacheDataSource.getAccessToken()
            ?: localDataSource.getAccessToken()
            ?: return Resource.Error(NullPointerException("User is Not logged in."))

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
            localDataSource.saveUserInfo(remote.data)
            cacheDataSource.saveUserInfoToCache(remote.data)
        }
        return remote
    }

    override suspend fun getPartnerInfo(): Resource<UserInfo> {
        try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val cache = cacheDataSource.getPartnerInfo()
            cache?.let { return Resource.Success(cache) }

            val partnerAccessToken = remoteDataSource.getPartnerInfo(accessToken).body()!!.accessToken
            val partnerInfo = remoteDataSource.getUserInfo(partnerAccessToken).body()!!.toUserInfo()

            cacheDataSource.savePartnerInfoToCache(partnerInfo)
            return Resource.Success(partnerInfo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun getCoupleAnniversary(): Resource<String> {
        try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val cache = cacheDataSource.getCoupleAnniversary()
            cache?.let { return Resource.Success(cache) }

            val local = localDataSource.getCoupleAnniversary()
            local?.let { return Resource.Success(local) }

            val remote = remoteDataSource.getCoupleAnniversary(accessToken)
                .body()!!.date.replace(". ", "/")

            localDataSource.saveCoupleAnniversary(remote)
            cacheDataSource.saveCoupleAnniversary(remote)
            return Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }


    override suspend fun checkUserInfoIsEntered(): Resource<Boolean> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.checkUserInfoIsEntered(accessToken = accessToken)

            Resource.Success(response.body()!!.get("userInfoEntered").asBoolean)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun updateUserInfo(
        nickname: String,
        age: Long,
        birthDate: String,
        anniversary: String
    ): Resource<StatusDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.updateUserInfo(
                accessToken = accessToken,
                nickname = nickname,
                age = age,
                birthDate = birthDate,
                anniversary = anniversary
            )

            val userInfo = when (val data = getUserInfo()) {
                is Resource.Success -> data.data.apply {
                    this.nickname = nickname
                    this.age = age
                    this.birth = birthDate
                }
                else -> UserInfo(
                    name = null,
                    nickname = nickname,
                    email = null,
                    age = age,
                    birth = birthDate,
                )
            }
            cacheDataSource.saveUserInfoToCache(userInfo)
            localDataSource.saveUserInfo(userInfo)

            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun updateMyEmotion(emotion: String): Resource<StatusDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val remote = remoteDataSource.updateMyEmotion(accessToken, emotion)

            val userInfoResource = getUserInfo()
            if (userInfoResource is Resource.Success) {
                val userInfo = userInfoResource.data
                userInfo.emotion = emotion.toEmotion()
                cacheDataSource.saveUserInfoToCache(userInfo)
                localDataSource.saveUserInfo(userInfo)
            }

            Resource.Success(remote.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun checkUserInCouple(): Resource<CoupleCheckDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.checkUserIsCouple(accessToken)

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

    override suspend fun sendPartnerCoupleRegistrationCode(partnerCode: String): Resource<PartnerCodeCheckDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.sendPartnerCoupleRegistrationCode(
                accessToken = accessToken,
                partnerCode = partnerCode
            )
            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }



    override suspend fun autoLogInWithGoogle(): Resource<AccessTokenDto> {
        return try {
            val idToken = localDataSource.getGoogleIdToken()
                ?: throw Exception("User is not auto logged in. Please re-log in.")

            val response = remoteDataSource.autoLogInWithGoogle(idToken)

            val accessTokenDto = response.body()!!
            localDataSource.saveAccessToken(accessTokenDto.accessToken)
            cacheDataSource.saveAccessTokenToCache(accessTokenDto.accessToken)
            Resource.Success(accessTokenDto)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun signUpWithGoogle(
        idToken: String,
        serverAuthCode: String,
        fcmToken: String
    ): Resource<SignUpDto> {
        return try {
            val response = remoteDataSource.signUpWithGoogle(idToken, serverAuthCode, fcmToken)

            val coupleRegistrationCode = response.body()!!.validCode
            coupleRegistrationCode?.let {
                localDataSource.saveCoupleRegistrationCode(it)
                cacheDataSource.saveCoupleRegistrationCode(it)
            }

            val accessToken = AccessTokenDto(response.body()!!.token)
            localDataSource.saveAccessToken(accessToken.accessToken)
            localDataSource.saveGoogleIdToken(idToken)
            cacheDataSource.saveAccessTokenToCache(accessToken.accessToken)
            Resource.Success(response.body()!!)
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
            val userInfo = localDataSource.getUserInfo()
                ?: throw NullPointerException("User info is not saved at database yet.")
            Resource.Success(userInfo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private suspend fun getUserInfoFromServer(accessToken: String): Resource<UserInfo> {
        return try {
            val response = remoteDataSource.getUserInfo(accessToken)
            Resource.Success(response.body()!!.toUserInfo())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


}