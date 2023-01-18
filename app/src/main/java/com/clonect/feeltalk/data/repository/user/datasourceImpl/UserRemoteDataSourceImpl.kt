package com.clonect.feeltalk.data.repository.user.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.api.GoogleAuthService
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.model.user.GoogleTokenRequest
import com.clonect.feeltalk.domain.model.user.GoogleTokens
import com.clonect.feeltalk.domain.model.user.LogInEmailRequest
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class UserRemoteDataSourceImpl(
    private val clonectService: ClonectService,
    private val googleAuthService: GoogleAuthService
): UserRemoteDataSource {
    override suspend fun getGoogleTokens(request: GoogleTokenRequest): Response<GoogleTokens> {
        return googleAuthService.fetchGoogleAuthInfo(request)
    }

    override suspend fun signUpWithEmail(request: SignUpEmailRequest): Response<UserInfo> {
        request.run {
            val bodyPart = hashMapOf(
                "email" to email.toRequestBody("text/plain".toMediaTypeOrNull()),
                "password" to password.toRequestBody("text/plain".toMediaTypeOrNull()),
                "name" to name.toRequestBody("text/plain".toMediaTypeOrNull()),
                "nickname" to nickname.toRequestBody("text/plain".toMediaTypeOrNull()),
                "age" to age.toRequestBody("text/plain".toMediaTypeOrNull()),
                "phone" to phone.toRequestBody("text/plain".toMediaTypeOrNull())
            )
            val profilePart = MultipartBody.Part
                .createFormData(
                    "image",
                    profile.name,
                    profile.asRequestBody()
                )

            return clonectService.signUpWithEmail(
                profileImage = profilePart,
                body = bodyPart
            )
        }
    }

    override suspend fun logInWithEmail(request: LogInEmailRequest): Response<UserInfo> {
        return clonectService.logInWithEmail(request)
    }
}