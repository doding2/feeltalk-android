package com.clonect.feeltalk.data.api

import com.clonect.feeltalk.domain.model.user.*
import com.clonect.feeltalk.domain.model.user.dto.CoupleCheckDto
import com.clonect.feeltalk.domain.model.user.dto.SendPartnerCoupleRegistrationCodeDto
import com.clonect.feeltalk.domain.model.user.dto.SignUpDto
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ClonectService {

    @GET("api/memberInfo/{accessToken}")
    suspend fun getUserInfo(
        @Path("accessToken") accessToken: String
    ): Response<UserInfo>

    @GET("api/couple/matched/{accessToken}")
    suspend fun checkUserIsCouple(
        @Path("accessToken") accessToken: String
    ): Response<CoupleCheckDto>
    
    @POST("api/member")
    suspend fun signUpWithGoogle(
        @Body body: JsonObject
    ): Response<SignUpDto>

    @POST("api/login")
    suspend fun autoLogInWithGoogle(
        @Body body: JsonObject
    ): Response<AccessToken>

    @POST("api/couple/match")
    suspend fun sendPartnerCoupleRegistrationCode(
        @Body body: JsonObject
    ): Response<SendPartnerCoupleRegistrationCodeDto>

}