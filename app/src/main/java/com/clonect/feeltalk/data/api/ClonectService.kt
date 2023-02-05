package com.clonect.feeltalk.data.api

import com.clonect.feeltalk.domain.model.encryption.LoadPartnerPrivateKeyDto
import com.clonect.feeltalk.domain.model.encryption.LoadPartnerPublicKeyDto
import com.clonect.feeltalk.domain.model.encryption.StatusResponse
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


    @POST("api/member/publicKey")
    suspend fun uploadMyPublicKey(
        @Body body: JsonObject
    ): Response<StatusResponse>

    @GET("api/member/partnerPublicKey/{accessToken}")
    suspend fun loadPartnerPublicKey(
        @Path("accessToken") accessToken: String
    ): Response<LoadPartnerPublicKeyDto>

    @POST("api/member/privateKey")
    suspend fun uploadMyPrivateKey(
        @Body body: JsonObject
    ): Response<StatusResponse>

    @GET("api/member/partnerPrivateKey/{accessToken}")
    suspend fun loadPartnerPrivateKey(
        @Path("accessToken") accessToken: String
    ): Response<LoadPartnerPrivateKeyDto>

}