package com.clonect.feeltalk.data.repository.user

import android.graphics.Bitmap
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.mapper.toEmotion
import com.clonect.feeltalk.data.mapper.toUserInfo
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.user.*
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val cacheDataSource: UserCacheDataSource
): UserRepository {

    override suspend fun getAccessToken(): Resource<String> {
        return cacheDataSource.getAccessToken()?.let { Resource.Success(it) }
            ?: localDataSource.getAccessToken()?.let { Resource.Success(it) }
             ?: Resource.Error(NullPointerException("User is not logged in."))
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

    @OptIn(FlowPreview::class)
    override suspend fun getPartnerInfoFlow(): Flow<Resource<UserInfo>> {
        val cacheFlow = channelFlow<Resource<UserInfo>> {
            val cache = cacheDataSource.getPartnerInfo()
            if (cache != null) {
                send(Resource.Success(cache))
            }
        }

        val remoteFlow = channelFlow {
            try {
                val accessToken = cacheDataSource.getAccessToken()
                    ?: localDataSource.getAccessToken()
                    ?: throw NullPointerException("User is Not logged in.")

                val partnerAccessToken = remoteDataSource.getPartnerInfo(accessToken).body()!!.accessToken
                val partnerInfo = remoteDataSource.getUserInfo(partnerAccessToken).body()!!.toUserInfo()
                cacheDataSource.savePartnerInfoToCache(partnerInfo)

                send(Resource.Success(partnerInfo))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                send(Resource.Error(e))
            }
        }

        return flowOf(cacheFlow, remoteFlow).flattenMerge()
    }


    override suspend fun breakUpCouple(): Resource<StatusDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val remote = remoteDataSource.breakUpCouple(accessToken).body()!!
            return Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun requestChangingPartnerEmotion(): Resource<StatusDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val remote = remoteDataSource.requestChangingPartnerEmotion(accessToken).body()!!
            return Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getCoupleAnniversary(): Resource<String> {
        try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val cache = cacheDataSource.getCoupleAnniversary()
            cache?.let { return Resource.Success(cache) }

            val remote = remoteDataSource.getCoupleAnniversary(accessToken).body()!!
            val anniversaryDate = StringBuilder(remote.date).run {
                insert(4 , '/')
                insert(7 , '/')
                toString()
            }

            cacheDataSource.saveCoupleAnniversary(anniversaryDate)
            return Resource.Success(anniversaryDate)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun updateMyNickname(nickname: String): Resource<StatusDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val remote = remoteDataSource.updateNickname(accessToken, nickname).body()!!

            val localUserInfo = localDataSource.getUserInfo()
            if (localUserInfo != null) {
                localUserInfo.nickname = nickname
                localDataSource.saveUserInfo(localUserInfo)
            }

            val cacheUserInfo = cacheDataSource.getUserInfo()
            if (cacheUserInfo != null) {
                cacheUserInfo.nickname = nickname
                cacheDataSource.saveUserInfoToCache(cacheUserInfo)
            }

            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun updateBirth(birth: String): Resource<StatusDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val remote = remoteDataSource.updateBirth(accessToken, birth).body()!!

            val localUserInfo = localDataSource.getUserInfo()
            if (localUserInfo != null) {
                localUserInfo.birth = birth
                localDataSource.saveUserInfo(localUserInfo)
            }

            val cacheUserInfo = cacheDataSource.getUserInfo()
            if (cacheUserInfo != null) {
                cacheUserInfo.birth = birth
                cacheDataSource.saveUserInfoToCache(cacheUserInfo)
            }

            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun updateCoupleAnniversary(coupleAnniversary: String): Resource<StatusDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val remote = remoteDataSource.updateCoupleAnniversary(accessToken, coupleAnniversary).body()!!
            cacheDataSource.saveCoupleAnniversary(coupleAnniversary)

            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun updateMyProfileImage(image: Bitmap): Resource<ProfileImageUrlDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val remote = remoteDataSource.updateUserProfileImage(accessToken, image).body()!!
            localDataSource.saveUserProfileUrl(remote.url)
            cacheDataSource.saveUserProfileUrl(remote.url)
            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getMyProfileImageUrl(): Resource<String> {
        try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val cache = cacheDataSource.getUserProfileUrl()
            cache?.let { return Resource.Success(it) }

            val local = localDataSource.getUserProfileUrl()
            local?.let {
                cacheDataSource.saveUserProfileUrl(it)
                return Resource.Success(it)
            }

            val remote = remoteDataSource.getUserProfileUrl(accessToken).body()!!.url
            localDataSource.saveUserProfileUrl(remote)
            cacheDataSource.saveUserProfileUrl(remote)
            return Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

    override suspend fun getPartnerProfileImageUrl(): Resource<String> {
        try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val cache = cacheDataSource.getPartnerProfileUrl()
            cache?.let { return Resource.Success(it) }

            val partnerAccessToken = remoteDataSource.getPartnerInfo(accessToken).body()!!.accessToken
            val remote = remoteDataSource.getUserProfileUrl(partnerAccessToken).body()!!.url
            cacheDataSource.savePartnerProfileUrl(remote)
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

    override suspend fun getCoupleRegistrationCode(withCache: Boolean): Resource<String> {
        try {
            val cache = cacheDataSource.getCoupleRegistrationCode()
            if (cache != null && withCache)
                return Resource.Success(cache)

            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            try {
                val remote = remoteDataSource.getCoupleRegistrationCode(accessToken).body()!!.coupleCode
                cacheDataSource.saveCoupleRegistrationCode(remote)
                localDataSource.saveCoupleRegistrationCode(remote)
                return Resource.Success(remote)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {}


            val local = localDataSource.getCoupleRegistrationCode()
            if (local != null) {
                cacheDataSource.saveCoupleRegistrationCode(local)
                return Resource.Success(local)
            }

            throw Exception("Fail To Load Couple Registration Code")
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

    override suspend fun updateFcmToken(fcmToken: String): Resource<StatusDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.updateFcmToken(accessToken, fcmToken)
            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
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


    override suspend fun signUpWithKakao(
        accessToken: String, fcmToken: String
    ): Resource<SignUpDto> {
        return try {
            val response = remoteDataSource.signUpWithKakao(accessToken, fcmToken).body()!!

            val coupleRegistrationCode = response.validCode
            coupleRegistrationCode?.let {
                localDataSource.saveCoupleRegistrationCode(it)
                cacheDataSource.saveCoupleRegistrationCode(it)
            }

            localDataSource.saveAccessToken(response.token)
            cacheDataSource.saveAccessTokenToCache(response.token)

            Resource.Success(response)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun autoLogInWithKakao(): Resource<AccessTokenDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.autoLogInWithKakao(accessToken)

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


    override suspend fun signUpWithNaver(
        accessToken: String,
        fcmToken: String,
    ): Resource<SignUpDto> {
        return try {
            val response = remoteDataSource.signUpWithNaver(accessToken, fcmToken).body()!!

            val coupleRegistrationCode = response.validCode
            coupleRegistrationCode?.let {
                localDataSource.saveCoupleRegistrationCode(it)
                cacheDataSource.saveCoupleRegistrationCode(it)
            }

            localDataSource.saveAccessToken(response.token)
            cacheDataSource.saveAccessTokenToCache(response.token)

            Resource.Success(response)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun autoLogInWithNaver(): Resource<AccessTokenDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.autoLogInWithNaver(accessToken)

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

    override suspend fun signUpWithApple(uuid: String, fcmToken: String): Resource<SignUpDto> {
        return try {
            val appleAccessToken = remoteDataSource.getAppleAccessToken(uuid).body()!!.accessToken
            val response = remoteDataSource.signUpWithApple(appleAccessToken, fcmToken).body()!!

            val coupleRegistrationCode = response.validCode
            coupleRegistrationCode?.let {
                localDataSource.saveCoupleRegistrationCode(it)
                cacheDataSource.saveCoupleRegistrationCode(it)
            }

            localDataSource.saveAccessToken(response.token)
            cacheDataSource.saveAccessTokenToCache(response.token)
            localDataSource.saveIsAppleLoggedIn(true)

            Resource.Success(response)
        } catch (e: CancellationException) {
            localDataSource.saveIsAppleLoggedIn(false)
            throw e
        } catch (e: Exception) {
            localDataSource.saveIsAppleLoggedIn(false)
            Resource.Error(e)
        }
    }

    override suspend fun autoLogInWithApple(): Resource<AccessTokenDto> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()
                ?: throw NullPointerException("User is Not logged in.")

            val response = remoteDataSource.autoLogInWithApple(accessToken)

            val accessTokenDto = response.body()!!
            localDataSource.saveAccessToken(accessTokenDto.accessToken)
            cacheDataSource.saveAccessTokenToCache(accessTokenDto.accessToken)
            localDataSource.saveIsAppleLoggedIn(true)

            Resource.Success(accessTokenDto)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun checkIsAppleLoggedIn(): Resource<Boolean> {
        return try {
            val accessToken = cacheDataSource.getAccessToken()
                ?: localDataSource.getAccessToken()

            val local = localDataSource.getAppleLoggedIn()
            if (local != null && accessToken != null) {
                return Resource.Success(local)
            }

            throw NullPointerException("User is not logged in with apple.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
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



    override suspend fun clearCoupleInfo(): Resource<Boolean> {
        return try {
            val isSuccessful = localDataSource.clearCoupleInfo()
            Resource.Success(isSuccessful)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun clearAllExceptKeys(): Resource<Boolean> {
        return try {
            val isSuccessful = localDataSource.clearAllExceptKeys()
            Resource.Success(isSuccessful)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}