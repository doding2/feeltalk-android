package com.clonect.feeltalk.data.repository.user.datasourceImpl

import android.graphics.Bitmap
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.data.utils.BitmapRequestBody
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.user.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.HttpException
import retrofit2.Response

class UserRemoteDataSourceImpl(
    private val clonectService: ClonectService,
): UserRemoteDataSource {

    override suspend fun getUserInfo(accessToken: String): Response<UserInfoDto> {
        val response = clonectService.getUserInfo(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun getPartnerInfo(accessToken: String): Response<AccessTokenDto> {
        val response = clonectService.getPartnerInfo(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun breakUpCouple(accessToken: String): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.breakUpCouple(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun requestChangingPartnerEmotion(accessToken: String): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.requestChangingPartnerEmotion(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    override suspend fun updateUserProfileImage(accessToken: String, image: Bitmap): Response<ProfileImageUrlDto> {
        val bitmapRequestBody = BitmapRequestBody(image)
        val response = clonectService.updateMyProfileImage(
            image = MultipartBody.Part.createFormData("image", "profile_image", bitmapRequestBody),
            accessToken = MultipartBody.Part.createFormData("accessToken", accessToken)
        )
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun getUserProfileUrl(accessToken: String): Response<ProfileImageUrlDto> {
        val response = clonectService.getUserProfileUrl(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun updateNickname(
        accessToken: String,
        nickname: String,
    ): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("nickName", nickname)
        }
        val response = clonectService.updateNickname(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun updateBirth(accessToken: String, birth: String): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("birth", birth)
        }
        val response = clonectService.updateBirth(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun updateCoupleAnniversary(
        accessToken: String,
        coupleAnniversary: String,
    ): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("createAt", coupleAnniversary)
        }
        val response = clonectService.updateCoupleAnniversary(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    override suspend fun getCoupleAnniversary(accessToken: String): Response<DDayDto> {
        val response = clonectService.getDDay(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    override suspend fun checkUserIsCouple(accessToken: String): Response<CoupleCheckDto> {
        val response =  clonectService.checkUserIsCouple(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun checkUserInfoIsEntered(accessToken: String): Response<JsonObject> {
        val response = clonectService.checkUserInfoIsEntered(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun updateUserInfo(
        accessToken: String,
        gender: String,
        nickname: String,
        age: Long,
        birthDate: String,
        anniversary: String
    ): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("gender", gender)
            addProperty("nickname", nickname)
            addProperty("age", age)
            addProperty("birth", birthDate)
            addProperty("coupleDay", anniversary)
        }
        val response = clonectService.updateUserInfo(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun updateMyEmotion(accessToken: String, emotion: String): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("emotion", emotion)
        }
        val response = clonectService.updateMyEmotion(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun getCoupleRegistrationCode(accessToken: String): Response<CoupleRegistrationCodeDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.getCoupleRegistrationCode(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    override suspend fun sendPartnerCoupleRegistrationCode(
        accessToken: String,
        partnerCode: String,
    ): Response<PartnerCodeCheckDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("coupleCode", partnerCode)
        }
        val response = clonectService.sendPartnerCoupleRegistrationCode(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun updateFcmToken(
        accessToken: String,
        fcmToken: String,
    ): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("fcmToken", fcmToken)
        }
        val response = clonectService.updateFcmToken(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    override suspend fun signUpWithGoogle(
        idToken: String,
        serverAuthCode: String,
        fcmToken: String
    ): Response<SignUpDto> {
        val body = JsonObject().apply {
            addProperty("idToken", idToken)
            addProperty("authCode", serverAuthCode)
            addProperty("fcmToken", fcmToken)
        }
        val response = clonectService.signUpWithGoogle(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun autoLogIn(accessToken: String): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.autoLogIn(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun autoLogInWithGoogle(idToken: String): Response<AccessTokenDto> {
        val body = JsonObject().apply {
            addProperty("idToken", idToken)
        }
        val response = clonectService.autoLogInWithGoogle(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    override suspend fun signUpWithKakao(
        accessToken: String,
        fcmToken: String
    ): Response<SignUpDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("fcmToken", fcmToken)
        }
        val response = clonectService.signUpWithKakao(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun autoLogInWithKakao(accessToken: String): Response<AccessTokenDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.autoLogInWithKakao(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    override suspend fun signUpWithNaver(
        accessToken: String,
        fcmToken: String,
    ): Response<SignUpDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("fcmToken", fcmToken)
        }
        val response = clonectService.signUpWithNaver(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun autoLogInWithNaver(accessToken: String): Response<AccessTokenDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.autoLogInWithNaver(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun signUpWithApple(accessToken: String, fcmToken: String): Response<SignUpDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("fcmToken", fcmToken)
        }
        val response = clonectService.signUpWithApple(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun autoLogInWithApple(accessToken: String): Response<AccessTokenDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.autoLogInWithApple(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun getAppleAccessToken(uuid: String): Response<AccessTokenDto> {
        val response = clonectService.getAppleAccessToken(uuid)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun leaveFeeltalk(accessToken: String): Response<StatusCodeDto> {
        val response = clonectService.leaveFeeltalk(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }
}