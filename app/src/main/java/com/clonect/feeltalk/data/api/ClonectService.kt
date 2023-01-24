package com.clonect.feeltalk.data.api

import com.clonect.feeltalk.domain.model.user.UserInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface ClonectService {

    @JvmSuppressWildcards
    @Multipart
    @POST("api/member")
    suspend fun signUpWithEmail(
        @Part profileImage: MultipartBody.Part,
        @PartMap body: Map<String, RequestBody>
    ): Response<UserInfo>

    @POST("api/login")
    suspend fun logInWithGoogle(
        @Body request: String
    ): Response<UserInfo>


    @GET("api/memberInfo/{accessToken}")
    suspend fun getUserInfo(
        @Path("accessToken") accessToken: String
    ): Response<UserInfo>
}