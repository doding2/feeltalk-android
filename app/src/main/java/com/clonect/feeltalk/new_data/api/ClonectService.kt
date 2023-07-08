package com.clonect.feeltalk.new_data.api

import com.clonect.feeltalk.common.ApiResponse
import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPrivateKeyDto
import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPublicKeyDto
import com.clonect.feeltalk.domain.model.dto.chat.ChatListItemDto2
import com.clonect.feeltalk.domain.model.dto.chat.SendChatDto2
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.encryption.AppLevelAesKeyDto
import com.clonect.feeltalk.domain.model.dto.encryption.RestorePrivateKeysDto
import com.clonect.feeltalk.domain.model.dto.encryption.TempPublicKeyDto
import com.clonect.feeltalk.domain.model.dto.news.NewsDto
import com.clonect.feeltalk.domain.model.dto.question.*
import com.clonect.feeltalk.domain.model.dto.user.*
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.new_domain.model.chat.SendTextChatDto
import com.clonect.feeltalk.new_domain.model.signIn.*
import com.clonect.feeltalk.new_domain.model.token.RenewTokenDto
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ClonectService {

    /** SignIn **/

    @GET("/api/v1/auto-login")
    suspend fun autoLogIn(
        @Header("Authorization") token: String
    ): Response<ApiResponse<AutoLogInDto>>

    @POST("/api/v1/re-login")
    suspend fun reLogIn(
        @Body body: JsonObject
    ): Response<ApiResponse<ReLogInDto>>

    @POST("/api/v1/sign-up")
    suspend fun signUp(
        @Body body: JsonObject
    ): Response<ApiResponse<SignUpDto>>

    @GET("/api/v1/member/invite-code")
    suspend fun getCoupleCode(
        @Header("Authorization") token: String
    ): Response<ApiResponse<CoupleCodeDto>>

    @POST("/api/v1/couple")
    suspend fun mathCouple(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<MatchCoupleDto>>

    /** Token **/

    @POST("/api/v1/renew-access-token")
    suspend fun renewToken(
        @Body body: JsonObject
    ): Response<ApiResponse<RenewTokenDto>>

    /** Chat **/

    @PUT("/api/v1/member/chatting-room-status")
    suspend fun changeChatRoomState(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<Unit>>

    @GET("/api/v1/chatting-message/last/page-no")
    suspend fun getLastChatPageNo(
        @Header("Authorization") token: String
    ): Response<ApiResponse<LastChatPageNoDto>>

    @POST("/api/v1/chatting-messages")
    suspend fun getChatList(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ChatListDto>>

    @POST("/api/v1/chatting-message/text")
    suspend fun sendTextChat(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<SendTextChatDto>>









    /** Old Apis **/

    /** User **/
    @GET("api/memberInfo/{accessToken}")
    suspend fun getUserInfo(
        @Path("accessToken") accessToken: String,
    ): Response<UserInfoDto>

    @GET("api/couple/partner/{accessToken}")
    suspend fun getPartnerInfo(
        @Path("accessToken") accessToken: String,
    ): Response<AccessTokenDto>

    @GET("api/isMemberAdditional/{accessToken}")
    suspend fun checkUserInfoIsEntered(
        @Path("accessToken") accessToken: String,
    ): Response<JsonObject>

    @GET("api/couple/matched/{accessToken}")
    suspend fun checkUserIsCouple(
        @Path("accessToken") accessToken: String,
    ): Response<CoupleCheckDto>

    @GET("api/DDay/{accessToken}")
    suspend fun getDDay(
        @Path("accessToken") accessToken: String,
    ): Response<DDayDto>

    @POST("api/member/additional")
    suspend fun updateUserInfo(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @POST("api/member/emotion")
    suspend fun updateMyEmotion(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @POST("api/couple/break")
    suspend fun breakUpCouple(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @POST("/api/couple/coupleCode")
    suspend fun getCoupleRegistrationCode(
        @Body body: JsonObject,
    ): Response<CoupleRegistrationCodeDto>

    @POST("api/couple/match")
    suspend fun sendPartnerCoupleRegistrationCode(
        @Body body: JsonObject,
    ): Response<PartnerCodeCheckDto>

    @POST("api/member/updateFcmToken")
    suspend fun updateFcmToken(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @POST("api/fcm/emotion")
    suspend fun requestChangingPartnerEmotion(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @GET("api/memberUrl/{accessToken}")
    suspend fun getUserProfileUrl(
        @Path("accessToken") accessToken: String,
    ): Response<ProfileImageUrlDto>

    @Multipart
    @POST("api/member/image")
    suspend fun updateMyProfileImage(
        @Part image: MultipartBody.Part,
        @Part accessToken: MultipartBody.Part,
    ): Response<ProfileImageUrlDto>

    @POST("api/member/update/NickName")
    suspend fun updateNickname(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @POST("api/member/update/birth")
    suspend fun updateBirth(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @POST("api/couple/update/startedDating")
    suspend fun updateCoupleAnniversary(
        @Body body: JsonObject,
    ): Response<StatusDto>


    /** Question **/
    @POST("/api/todayQuestion")
    suspend fun getTodayQuestion(
        @Body body: JsonObject,
    ): Response<TodayQuestionDto>

    @POST("/api/chattingRoom")
    suspend fun sendQuestionAnswer(
        @Body body: JsonObject,
    ): Response<SendQuestionDto>

    @POST("/api/isAnswer")
    suspend fun getTodayQuestionAnswers(
        @Body body: JsonObject,
    ): Response<TodayQuestionAnswersDto>

    @POST("/api/chattingRoom/answers")
    suspend fun getQuestionAnswers(
        @Body body: JsonObject,
    ): Response<QuestionAnswersDto>

    @POST("/api/question/detail")
    suspend fun getQuestionDetail(
        @Body body: JsonObject
    ): Response<QuestionDetailDto>


    /** Chat **/
    // "accessToken": String, "question": String, "message": String
    @POST("/api/chattingMessage")
    suspend fun sendChat(
        @Body body: JsonObject,
    ): Response<SendChatDto2>

    @GET("/api/chattingRoom/{accessToken}")
    suspend fun getChattingRoomList(
        @Path("accessToken") accessToken: String,
    ): Response<QuestionListDto>

    @POST("/api/chattingMessageList")
    suspend fun getChatList(
        @Body body: JsonObject,
    ): Response<List<ChatListItemDto2>>


    /**  News  **/
    @POST("api/alert/list")
    suspend fun getNewsList(
        @Body body: JsonObject,
    ): Response<List<NewsDto>>


    /** Sign Up **/

    @POST("api/member")
    suspend fun signUpWithGoogle(
        @Body body: JsonObject,
    ): Response<OldSignUpDto>

    @POST("api/login")
    suspend fun autoLogIn(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @POST("api/login")
    suspend fun autoLogInWithGoogle(
        @Body body: JsonObject,
    ): Response<AccessTokenDto>

    @POST("/api/member/kakao")
    suspend fun signUpWithKakao(
        @Body body: JsonObject,
    ): Response<OldSignUpDto>

    @POST("/api/login/kakao")
    suspend fun autoLogInWithKakao(
        @Body body: JsonObject,
    ): Response<AccessTokenDto>

    @POST("/api/member/naver")
    suspend fun signUpWithNaver(
        @Body body: JsonObject,
    ): Response<OldSignUpDto>

    @POST("/api/login/naver")
    suspend fun autoLogInWithNaver(
        @Body body: JsonObject,
    ): Response<AccessTokenDto>

    @POST("/api/member/apple")
    suspend fun signUpWithApple(
        @Body body: JsonObject,
    ): Response<OldSignUpDto>

    @POST("/api/login/apple")
    suspend fun autoLogInWithApple(
        @Body body: JsonObject,
    ): Response<AccessTokenDto>

    @GET("api/member/appleState/{uuid}")
    suspend fun getAppleAccessToken(
        @Path("uuid") uuid: String,
    ): Response<AccessTokenDto>

    @DELETE("api/member-one/{accessToken}")
    suspend fun leaveFeeltalk(
        @Path("accessToken") accessToken: String
    ): Response<StatusCodeDto>


    /** Encryption **/
    @POST("api/member/clientAESKey")
    suspend fun getAppLevelAesKey(
        @Body body: JsonObject,
    ): Response<AppLevelAesKeyDto>

    @POST("api/member/publicKey")
    suspend fun uploadMyPublicKey(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @GET("api/member/partnerPublicKey/{accessToken}")
    suspend fun loadPartnerPublicKey(
        @Path("accessToken") accessToken: String,
    ): Response<LoadPartnerPublicKeyDto>

    @POST("api/member/privateKey")
    suspend fun uploadMyPrivateKey(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @GET("api/member/partnerPrivateKey/{accessToken}")
    suspend fun loadPartnerPrivateKey(
        @Path("accessToken") accessToken: String,
    ): Response<LoadPartnerPrivateKeyDto>


    /** Restore Encryption Keys **/
    @POST("api/fcm/KeyTrade")
    suspend fun requestKeyRestoring(
        @Body body: JsonObject
    ): Response<StatusDto>

    @POST("api/member/tempPublicKey")
    suspend fun uploadTempKey(
        @Body body: JsonObject,
    ): Response<StatusDto>

    @GET("api/member/PublicKey/{accessToken}")
    suspend fun loadMyPublicKey(
        @Path("accessToken") accessToken: String,
    ): Response<LoadPartnerPublicKeyDto>

    @GET("api/member/BothKey/{accessToken}")
    suspend fun restorePrivateKeys(
        @Path("accessToken") accessToken: String,
    ): Response<RestorePrivateKeysDto>


    /** Send Encryption Keys for Restoring **/
    @POST("api/fcm/KeyTradeOk")
    suspend fun acceptKeyRestoring(
        @Body body: JsonObject
    ): Response<StatusDto>

    @GET("api/member/partnerTempPublicKey/{accessToken}")
    suspend fun loadTempKey(
        @Path("accessToken") accessToken: String,
    ): Response<TempPublicKeyDto>

    @POST("api/member/tempPrivateKey")
    suspend fun uploadTempEncryptedPrivateKey(
        @Body body: JsonObject,
    ): Response<StatusDto>

}