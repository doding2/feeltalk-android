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

    /** User **/
    @GET("api/memberInfo/{accessToken}")
    suspend fun getUserInfo(
        @Path("accessToken") accessToken: String
    ): Response<UserInfo>

    @GET("api/isMemberAdditional/{accessToken}")
    suspend fun checkUserInfoIsEntered(
        @Path("accessToken") accessToken: String
    ): Response<String>

    @GET("api/couple/matched/{accessToken}")
    suspend fun checkUserIsCouple(
        @Path("accessToken") accessToken: String
    ): Response<CoupleCheckDto>

    @GET("api/DDay/{accessToken}")
    suspend fun getDDay(
        @Path("accessToken") accessToken: String
    ): Response<String>


    /** Question **/
    // {"accessToken": ""}
    @POST("/api/todayQuestion")
    suspend fun getTodayQuestion(
        @Body body: JsonObject
    ): Response<String>

    // "accessToken": String, "question": String, "answer": String
    @POST("/api/chattingRoom")
    suspend fun sendQuestionAnswer(
        @Body body: JsonObject
    ): Response<String>

    // fcm
    @POST("/api/fcm/answerRequest")
    suspend fun requestQuestionAnswerToPartner(
        @Body body: JsonObject
    ): Response<String>


    /** Chat **/
    // "accessToken": String, "question": String, "message": String
    @POST("/api/fcm/chattingMessage")
    suspend fun sendChat(
        @Body body: JsonObject
    ): Response<String>

    @GET("/api/chattingRoom/{accessToken}")
    suspend fun getChattingRoomList(
        @Path("accessToken") accessToken: String
    ): Response<String>

    // "accessToken": String, "question": String
    @GET("/api/chattingMessageList")
    suspend fun getChatList(
        @Body body: JsonObject
    ): Response<String>


    /** Sign Up **/
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


    /** Encryption **/
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