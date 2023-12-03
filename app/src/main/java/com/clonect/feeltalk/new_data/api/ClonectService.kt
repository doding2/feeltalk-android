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
import com.clonect.feeltalk.new_domain.model.account.*
import com.clonect.feeltalk.new_domain.model.challenge.*
import com.clonect.feeltalk.new_domain.model.chat.*
import com.clonect.feeltalk.new_domain.model.newAccount.GetUserStatusNewResponse
import com.clonect.feeltalk.new_domain.model.newAccount.LogInNewResponse
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfoDto
import com.clonect.feeltalk.new_domain.model.question.LastQuestionPageNoDto
import com.clonect.feeltalk.new_domain.model.question.PressForAnswerChatResponse
import com.clonect.feeltalk.new_domain.model.question.QuestionDto
import com.clonect.feeltalk.new_domain.model.question.QuestionListDto
import com.clonect.feeltalk.new_domain.model.signal.ChangeMySignalResponse
import com.clonect.feeltalk.new_domain.model.signal.SignalResponse
import com.clonect.feeltalk.new_domain.model.token.RenewTokenDto
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ClonectService {

    /** New Account **/

    @POST("/api/v1/login")
    suspend fun logInNew(
        @Body body: JsonObject
    ): Response<ApiResponse<LogInNewResponse>>

    @GET("/api/v1/member/status")
    suspend fun getUserStatusNew(
        @Header("Authorization") accessToken: String
    ): Response<ApiResponse<GetUserStatusNewResponse>>

    @POST("/api/v1/signup")
    suspend fun signUpNew(
        @Header("Authorization") accessToken: String,
        @Body body: JsonObject
    ): Response<Unit>

    @POST("/api/v1/reissue")
    suspend fun reissueToken(
        @Header("Authorization") accessToken: String,
        @Header("Authorization-refresh") refreshToken: String
    ): Response<ApiResponse<LogInNewResponse>>



    /** Account **/

    @GET("/api/v1/auto-login")
    suspend fun autoLogIn(
        @Header("Authorization") token: String,
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
    ): Response<ApiResponse<Unit>>

    @GET("/api/v1/member")
    suspend fun getMyInfo(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<MyInfoDto>>

    @GET("/api/v1/logout")
    suspend fun logOut(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<Unit>>

    @HTTP(method = "DELETE", path = "api/v1/member", hasBody = true)
    suspend fun deleteMyAccount(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<Unit>>

    @POST("/api/v1/member/config/password")
    suspend fun setupPassword(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<Unit>>

    @PUT("/api/v1/member/config/password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<Unit>>

    @POST("/api/v1/member/config/check-password")
    suspend fun validatePassword(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ValidatePasswordDto>>

    @GET("/api/v1/member/config/password")
    suspend fun getPassword(
        @Header("Authorization") token: String
    ): Response<ApiResponse<GetPasswordDto>>

    @PUT("/api/v1/member/config/lock")
    suspend fun unlockPassword(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>

    @GET("/api/v1/member/config/question-type")
    suspend fun getLockResetQuestion(
        @Header("Authorization") token: String
    ): Response<ApiResponse<LockResetQuestionDto>>

    @POST("/api/v1/member/config/valid-answer")
    suspend fun validateLockResetAnswer(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ValidateLockAnswerDto>>

    @DELETE("")
    suspend fun breakUpCouple(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>

    @POST("/api/v1/comment")
    suspend fun submitSuggestion(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<Unit>>

    @GET("/api/v1/member/service-data")
    suspend fun getServiceDataCount(
        @Header("Authorization") token: String
    ): Response<ApiResponse<ServiceDataCountDto>>

    @POST("api/v1/chatting-room/reset-password")
    suspend fun unlockPartnerPassword(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<UnlockPartnerPasswordResponse>>


    /** Token **/

    @POST("/api/v1/renew-access-token")
    suspend fun renewToken(
        @Body body: JsonObject
    ): Response<ApiResponse<RenewTokenDto>>

    @PUT("/api/v1/member/fcm-token")
    suspend fun updateFcmToken(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<Unit>>

    /** Chat **/

    @GET("/api/v1/chatting-message/partner/last")
    suspend fun getPartnerLastChat(
        @Header("Authorization") token: String
    ): Response<ApiResponse<PartnerLastChatDto>>

    @PUT("/api/v1/member/chatting-room-status")
    suspend fun changeChatRoomState(
        @Header("Authorization") token: String,
        @Body body: JsonObject,
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

    @Multipart
    @POST("/api/v1/chatting-message/voice")
    suspend fun sendVoiceChat(
        @Header("Authorization") token: String,
        @Part data: MultipartBody.Part
    ): Response<ApiResponse<SendVoiceChatDto>>

    @Multipart
    @POST("/api/v1/chatting-message/voice")
    suspend fun sendImageChat(
        @Header("Authorization") token: String,
        @Part data: MultipartBody.Part
    ): Response<ApiResponse<SendImageChatResponse>>

    @POST("api/v1/chatting-message/reset-partner-password")
    suspend fun sendResetPartnerPasswordChat(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<SendResetPartnerPasswordChatResponse>>


    /** Question **/

    @GET("/api/v1/couple-question/last/page-no")
    suspend fun getLastQuestionPageNo(
        @Header("Authorization") token: String
    ): Response<ApiResponse<LastQuestionPageNoDto>>

    @POST("/api/v1/couple-questions")
    suspend fun getQuestionList(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<QuestionListDto>>

    @GET("/api/v1/couple-question/{index}")
    suspend fun getQuestion(
        @Header("Authorization") token: String,
        @Path("index") index: Long
    ): Response<ApiResponse<QuestionDto>>

    @GET("/api/v1/couple-question/today")
    suspend fun getTodayQuestion(
        @Header("Authorization") token: String
    ): Response<ApiResponse<QuestionDto>>

    @PUT("/api/v1/couple-question")
    suspend fun answerQuestion(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<Unit>>

    @POST("/api/v1/couple-question/chase-up")
    suspend fun pressForAnswer(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<PressForAnswerChatResponse>>

    @POST("/api/v1/chatting-message/question")
    suspend fun shareQuestion(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ShareQuestionChatDto>>


    /** Challenge **/

    @GET("/api/v1/challenge/in-progress/last/page-no")
    suspend fun getLastOngoingChallengePageNo(
        @Header("Authorization") token: String
    ): Response<ApiResponse<LastChallengePageNoDto>>

    @POST("/api/v1/challenges/in-progress")
    suspend fun getOngoingChallengeList(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ChallengeListDto>>

    @GET("/api/v1/challenge/done/last/page-no")
    suspend fun getLastCompletedChallengePageNo(
        @Header("Authorization") token: String
    ): Response<ApiResponse<LastChallengePageNoDto>>

    @POST("/api/v1/challenges/done")
    suspend fun getCompletedChallengeList(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ChallengeListDto>>

    @POST("/api/v1/challenge")
    suspend fun addChallenge(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ChallengeChatResponse>>

    @PUT("/api/v1/challenge")
    suspend fun modifyChallenge(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<Unit>>

    @HTTP(method = "DELETE", path = "/api/v1/challenge", hasBody = true)
    suspend fun deleteChallenge(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<Unit>>

    @PUT("/api/v1/challenge/complete")
    suspend fun completeChallenge(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ChallengeChatResponse>>

    @GET("api/v1/challenge/{index}")
    suspend fun getChallenge(
        @Header("Authorization") token: String,
        @Path("index") index: Long,
    ): Response<ApiResponse<ChallengeDto>>

    @GET("/api/v1/challenge/count")
    suspend fun getChallengeCount(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<ChallengeCountDto>>

    @GET("/api/v1/member/config")
    suspend fun getConfigurationInfo(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<ConfigurationInfoDto>>

    @POST("api/v1/chatting-message/challenge")
    suspend fun shareChallenge(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ShareChallengeChatResponse>>


    /** Partner **/
    @GET("/api/v1/member/partner")
    suspend fun getPartnerInfo(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<PartnerInfoDto>>


    /** Signal **/

    @GET("/api/v1/member/signal")
    suspend fun getMySignal(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<SignalResponse>>

    @GET("/api/v1/member/partner/signal")
    suspend fun getPartnerSignal(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<SignalResponse>>

    @POST("/api/v1/chatting-message/signal")
    suspend fun changeMySignal(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<ApiResponse<ChangeMySignalResponse>>





    /** Old Apis **/

    /** User **/
    @GET("api/memberInfo/{accessToken}")
    suspend fun getUserInfo(
        @Path("accessToken") accessToken: String,
    ): Response<UserInfoDto>

    @GET("api/couple/partner/{accessToken}")
    suspend fun getPartnerInfo2(
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
    suspend fun updateFcmToken2(
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
    ): Response<QuestionListDto2>

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