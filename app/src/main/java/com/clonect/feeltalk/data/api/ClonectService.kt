package com.clonect.feeltalk.data.api

import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPrivateKeyDto
import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPublicKeyDto
import com.clonect.feeltalk.domain.model.dto.chat.ChatListItemDto
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.encryption.AppLevelAesKeyDto
import com.clonect.feeltalk.domain.model.dto.news.NewsDto
import com.clonect.feeltalk.domain.model.dto.question.QuestionAnswersDto
import com.clonect.feeltalk.domain.model.dto.question.QuestionListDto
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import com.clonect.feeltalk.domain.model.dto.question.TodayQuestionDto
import com.clonect.feeltalk.domain.model.dto.user.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ClonectService {

    /** User **/
    @GET("api/memberInfo/{accessToken}")
    suspend fun getUserInfo(
        @Path("accessToken") accessToken: String
    ): Response<UserInfoDto>

    @GET("api/couple/partner/{accessToken}")
    suspend fun getPartnerInfo(
        @Path("accessToken") accessToken: String
    ): Response<AccessTokenDto>

    @GET("api/isMemberAdditional/{accessToken}")
    suspend fun checkUserInfoIsEntered(
        @Path("accessToken") accessToken: String
    ): Response<JsonObject>

    @GET("api/couple/matched/{accessToken}")
    suspend fun checkUserIsCouple(
        @Path("accessToken") accessToken: String
    ): Response<CoupleCheckDto>

    @GET("api/DDay/{accessToken}")
    suspend fun getDDay(
        @Path("accessToken") accessToken: String
    ): Response<DDayDto>

    @POST("api/member/additional")
    suspend fun updateUserInfo(
        @Body body: JsonObject
    ): Response<StatusDto>

    @POST("api/member/emotion")
    suspend fun updateMyEmotion(
        @Body body: JsonObject
    ): Response<StatusDto>

    @POST("api/couple/break")
    suspend fun breakUpCouple(
        @Body body: JsonObject
    ): Response<StatusDto>

    @POST("/api/couple/coupleCode")
    suspend fun getCoupleRegistrationCode(
        @Body body: JsonObject
    ): Response<CoupleRegistrationCodeDto>

    @POST("api/couple/match")
    suspend fun sendPartnerCoupleRegistrationCode(
        @Body body: JsonObject
    ): Response<PartnerCodeCheckDto>

    @POST("api/member/updateFcmToken")
    suspend fun updateFcmToken(
        @Body body: JsonObject
    ): Response<StatusDto>

    @POST("api/fcm/emotion")
    suspend fun requestChangingPartnerEmotion(
        @Body body: JsonObject
    ): Response<StatusDto>

    @GET("/api/memberUrl/{accessToken}")
    suspend fun getUserProfileUrl(
        @Path("accessToken") accessToken: String
    ): Response<ProfileImageUrlDto>

    @Multipart
    @POST("/api/member/image")
    suspend fun updateMyProfileImage(
        @Part image: MultipartBody.Part,
        @Part accessToken: MultipartBody.Part
    ): Response<ProfileImageUrlDto>


    /** Question **/
    @POST("/api/todayQuestion")
    suspend fun getTodayQuestion(
        @Body body: JsonObject
    ): Response<TodayQuestionDto>

    @POST("/api/chattingRoom")
    suspend fun sendQuestionAnswer(
        @Body body: JsonObject
    ): Response<SendQuestionDto>

    @POST("/api/isAnswer")
    suspend fun getTodayQuestionAnswers(
        @Body body: JsonObject
    ): Response<QuestionAnswersDto>

    // fcm
    @POST("/api/fcm/answerRequest")
    suspend fun requestQuestionAnswerToPartner(
        @Body body: JsonObject
    ): Response<String>


    /** Chat **/
    // "accessToken": String, "question": String, "message": String
    @POST("/api/chattingMessage")
    suspend fun sendChat(
        @Body body: JsonObject
    ): Response<SendChatDto>

    @GET("/api/chattingRoom/{accessToken}")
    suspend fun getChattingRoomList(
        @Path("accessToken") accessToken: String
    ): Response<QuestionListDto>

    @POST("/api/chattingMessageList")
    suspend fun getChatList(
        @Body body: JsonObject
    ): Response<List<ChatListItemDto>>


    /**  News  **/
    @POST("api/alert/list")
    suspend fun getNewsList(
        @Body body: JsonObject
    ): Response<List<NewsDto>>



    /** Sign Up **/

    @POST("api/member")
    suspend fun signUpWithGoogle(
        @Body body: JsonObject
    ): Response<SignUpDto>

    @POST("api/login")
    suspend fun autoLogInWithGoogle(
        @Body body: JsonObject
    ): Response<AccessTokenDto>

    @POST("/api/member/kakao")
    suspend fun signUpWithKakao(
        @Body body: JsonObject
    ): Response<SignUpDto>

    @POST("/api/login/kakao")
    suspend fun autoLogInWithKakao(
        @Body body: JsonObject
    ): Response<AccessTokenDto>

    @POST("/api/member/naver")
    suspend fun signUpWithNaver(
        @Body body: JsonObject
    ): Response<SignUpDto>

    @POST("/api/login/naver")
    suspend fun autoLogInWithNaver(
        @Body body: JsonObject
    ): Response<AccessTokenDto>

    @POST("/api/member/apple")
    suspend fun signUpWithApple(
        @Body body: JsonObject
    ): Response<SignUpDto>

    @POST("/api/login/apple")
    suspend fun autoLogInWithApple(
        @Body body: JsonObject
    ): Response<AccessTokenDto>

    @GET("api/member/appleState/{uuid}")
    suspend fun getAppleAccessToken(
        @Path("uuid") uuid: String
    ): Response<AccessTokenDto>


    /** Encryption **/
    @POST("api/member/clientAESKey")
    suspend fun getAppLevelAesKey(
        @Body body: JsonObject
    ): Response<AppLevelAesKeyDto>

    @POST("api/member/publicKey")
    suspend fun uploadMyPublicKey(
        @Body body: JsonObject
    ): Response<StatusDto>

    @GET("api/member/partnerPublicKey/{accessToken}")
    suspend fun loadPartnerPublicKey(
        @Path("accessToken") accessToken: String
    ): Response<LoadPartnerPublicKeyDto>

    @POST("api/member/privateKey")
    suspend fun uploadMyPrivateKey(
        @Body body: JsonObject
    ): Response<StatusDto>

    @GET("api/member/partnerPrivateKey/{accessToken}")
    suspend fun loadPartnerPrivateKey(
        @Path("accessToken") accessToken: String
    ): Response<LoadPartnerPrivateKeyDto>


}