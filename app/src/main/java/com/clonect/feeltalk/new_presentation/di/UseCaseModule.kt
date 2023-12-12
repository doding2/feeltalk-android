package com.clonect.feeltalk.new_presentation.di

import android.content.Context
import android.content.SharedPreferences
import com.clonect.feeltalk.domain.repository.*
import com.clonect.feeltalk.domain.usecase.*
import com.clonect.feeltalk.domain.usecase.chat.GetChatListUseCase2
import com.clonect.feeltalk.domain.usecase.chat.ReloadChatListUseCase
import com.clonect.feeltalk.domain.usecase.chat.SaveChatUseCase
import com.clonect.feeltalk.domain.usecase.chat.SendChatUseCase
import com.clonect.feeltalk.domain.usecase.encryption.*
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.domain.usecase.news.GetNewsListUseCase
import com.clonect.feeltalk.domain.usecase.question.*
import com.clonect.feeltalk.domain.usecase.user.*
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.partner.PartnerRepository
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository
import com.clonect.feeltalk.new_domain.repository.signal.SignalRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.clonect.feeltalk.new_domain.usecase.account.*
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.*
import com.clonect.feeltalk.new_domain.usecase.chat.*
import com.clonect.feeltalk.new_domain.usecase.newAccount.GetUserStatusNewUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.LogInAppleUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.LogInNewUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.RequestAdultAuthCodeUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.RetryRequestAdultAuthCodeUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.SignUpNewUseCase
import com.clonect.feeltalk.new_domain.usecase.newAccount.VerifyAdultAuthCodeUseCase
import com.clonect.feeltalk.new_domain.usecase.partner.GetPartnerInfoFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.partner.GetPartnerInfoUseCase
import com.clonect.feeltalk.new_domain.usecase.question.*
import com.clonect.feeltalk.new_domain.usecase.signal.ChangeMySignalUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.ChangePartnerSignalCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetMySignalCacheFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetMySignalUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetPartnerSignalFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetPartnerSignalUseCase
import com.clonect.feeltalk.new_domain.usecase.token.CacheSocialTokenUseCase
import com.clonect.feeltalk.new_domain.usecase.token.GetCachedSocialTokenUseCase
import com.clonect.feeltalk.new_domain.usecase.token.UpdateFcmTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    /** New Account **/

    @Singleton
    @Provides
    fun providesLogInNewUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): LogInNewUseCase {
        return LogInNewUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesLogInAppleUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): LogInAppleUseCase {
        return LogInAppleUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesGetUserStatusNewUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): GetUserStatusNewUseCase {
        return GetUserStatusNewUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesSignUpNewUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): SignUpNewUseCase {
        return SignUpNewUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesRequestAdultAuthCodeUseCase(accountRepository: AccountRepository): RequestAdultAuthCodeUseCase {
        return RequestAdultAuthCodeUseCase(accountRepository)
    }

    @Singleton
    @Provides
    fun providesRetryRequestAdultAuthCodeUseCase(accountRepository: AccountRepository): RetryRequestAdultAuthCodeUseCase {
        return RetryRequestAdultAuthCodeUseCase(accountRepository)
    }

    @Singleton
    @Provides
    fun providesVerifyAdultAuthCodeUseCase(accountRepository: AccountRepository): VerifyAdultAuthCodeUseCase {
        return VerifyAdultAuthCodeUseCase(accountRepository)
    }




    /** Token **/

    @Singleton
    @Provides
    fun providesCacheSocialTokenUseCase(tokenRepository: TokenRepository): CacheSocialTokenUseCase {
        return CacheSocialTokenUseCase(tokenRepository)
    }

    @Singleton
    @Provides
    fun providesGetCachedSocialTokenUseCase(tokenRepository: TokenRepository): GetCachedSocialTokenUseCase {
        return GetCachedSocialTokenUseCase(tokenRepository)
    }

    @Singleton
    @Provides
    fun providesUpdateFcmTokenUseCase(tokenRepository: TokenRepository): UpdateFcmTokenUseCase {
        return UpdateFcmTokenUseCase(tokenRepository)
    }


    /** Account **/


    @Singleton
    @Provides
    fun providesAutoLogInUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): AutoLogInUseCase {
        return AutoLogInUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesReLogInUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): ReLogInUseCase {
        return ReLogInUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesSignUpUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): SignUpUseCase {
        return SignUpUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesGetCoupleCodeUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): GetCoupleCodeUseCase {
        return GetCoupleCodeUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesMatchCoupleUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): MatchCoupleUseCase {
        return MatchCoupleUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesLockAccountUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): LockAccountUseCase {
        return LockAccountUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesMatchPasswordUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): MatchPasswordUseCase {
        return MatchPasswordUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesCheckAccountLockedUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): CheckAccountLockedUseCase {
        return CheckAccountLockedUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesGetLockQAUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): GetLockQAUseCase {
        return GetLockQAUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesUnlockAccountUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): UnlockAccountUseCase {
        return UnlockAccountUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesUpdateAccountLockPasswordUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): UpdateAccountLockPasswordUseCase {
        return UpdateAccountLockPasswordUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesGetMyInfoUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): GetMyInfoUseCase {
        return GetMyInfoUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesGetConfigurationInfoUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): GetConfigurationInfoUseCase {
        return GetConfigurationInfoUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesLogOutUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): LogOutUseCase {
        return LogOutUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesDeleteMyAccountUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): DeleteMyAccountUseCase {
        return DeleteMyAccountUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesValidateLockResetAnswerUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): ValidateLockResetAnswerUseCase {
        return ValidateLockResetAnswerUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesCheckAccountLockedFlowUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): CheckAccountLockedFlowUseCase {
        return CheckAccountLockedFlowUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesBreakUpCoupleUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): BreakUpCoupleUseCase {
        return BreakUpCoupleUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesSubmitSuggestionUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): SubmitSuggestionUseCase {
        return SubmitSuggestionUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesGetServiceDataCountUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): GetServiceDataCountUseCase {
        return GetServiceDataCountUseCase(tokenRepository, accountRepository)
    }

    @Singleton
    @Provides
    fun providesSetCoupleCreatedUseCase(accountRepository: AccountRepository): SetCoupleCreatedUseCase {
        return SetCoupleCreatedUseCase(accountRepository)
    }

    @Singleton
    @Provides
    fun providesGetCoupleCreatedFlowUseCase(accountRepository: AccountRepository): GetCoupleCreatedFlowUseCase {
        return GetCoupleCreatedFlowUseCase(accountRepository)
    }

    @Singleton
    @Provides
    fun providesUnlockPartnerPasswordUseCase(tokenRepository: TokenRepository, accountRepository: AccountRepository): UnlockPartnerPasswordUseCase {
        return UnlockPartnerPasswordUseCase(tokenRepository, accountRepository)
    }



    /** Partner **/

    @Singleton
    @Provides
    fun providesGetPartnerInfoUseCase(tokenRepository: TokenRepository, partnerRepository: PartnerRepository): GetPartnerInfoUseCase {
        return GetPartnerInfoUseCase(tokenRepository, partnerRepository)
    }

    @Singleton
    @Provides
    fun providesGetPartnerInfoFlowUseCase(tokenRepository: TokenRepository, partnerRepository: PartnerRepository): GetPartnerInfoFlowUseCase {
        return GetPartnerInfoFlowUseCase(tokenRepository, partnerRepository)
    }


    /** AppSettings **/

    @Singleton
    @Provides
    fun providesGetAppSettingsUseCase(
        @Named("AppSettings")
        pref: SharedPreferences,
        appLevelEncryptHelper: AppLevelEncryptHelper
    ): GetAppSettingsUseCase {
        return GetAppSettingsUseCase(pref, appLevelEncryptHelper)
    }

    @Singleton
    @Provides
    fun providesSaveAppSettingsUseCase(
        @Named("AppSettings")
        pref: SharedPreferences,
        appLevelEncryptHelper: AppLevelEncryptHelper,
    ): SaveAppSettingsUseCase {
        return SaveAppSettingsUseCase(pref, appLevelEncryptHelper)
    }


    /** Chat **/

    @Singleton
    @Provides
    fun providesGetPartnerLastChatUseCase(tokenRepository: TokenRepository, chatRepository: ChatRepository): GetPartnerLastChatUseCase {
        return GetPartnerLastChatUseCase(tokenRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesChangeChatRoomStateUseCase(tokenRepository: TokenRepository, chatRepository: ChatRepository): ChangeMyChatRoomStateUseCase {
        return ChangeMyChatRoomStateUseCase(tokenRepository, chatRepository)
    }
    @Singleton
    @Provides
    fun providesGetMyChatRoomStateFlowUseCase(chatRepository: ChatRepository): GetMyChatRoomStateCacheUseCase {
        return GetMyChatRoomStateCacheUseCase(chatRepository)
    }

    @Singleton
    @Provides
    fun providesGetLastChatPageNoUseCase(tokenRepository: TokenRepository, chatRepository: ChatRepository): GetLastChatPageNoUseCase {
        return GetLastChatPageNoUseCase(tokenRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesGetChatListUseCase(tokenRepository: TokenRepository, chatRepository: ChatRepository): GetChatListUseCase {
        return GetChatListUseCase(tokenRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesGetPagingChatUseCase(chatRepository: ChatRepository): GetPagingChatUseCase {
        return GetPagingChatUseCase(chatRepository)
    }

    @Singleton
    @Provides
    fun providesSendTextChatUseCase(tokenRepository: TokenRepository, chatRepository: ChatRepository): SendTextChatUseCase {
        return SendTextChatUseCase(tokenRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesSendVoiceChatUseCase(tokenRepository: TokenRepository, chatRepository: ChatRepository): SendVoiceChatUseCase {
        return SendVoiceChatUseCase(tokenRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesSendImageChatUseCase(tokenRepository: TokenRepository, chatRepository: ChatRepository): SendImageChatUseCase {
        return SendImageChatUseCase(tokenRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesSendResetPartnerPasswordChatUseCase(tokenRepository: TokenRepository, chatRepository: ChatRepository): SendResetPartnerPasswordChatUseCase {
        return SendResetPartnerPasswordChatUseCase(tokenRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesAddNewChatCacheUseCase(chatRepository: ChatRepository): AddNewChatCacheUseCase {
        return AddNewChatCacheUseCase(chatRepository)
    }

    @Singleton
    @Provides
    fun providesGetNewChatFlowUseCase(chatRepository: ChatRepository): GetNewChatFlowUseCase {
        return GetNewChatFlowUseCase(chatRepository)
    }

    @Singleton
    @Provides
    fun providesChangePartnerChatRoomStateCacheUseCase(chatRepository: ChatRepository): ChangePartnerChatRoomStateCacheUseCase {
        return ChangePartnerChatRoomStateCacheUseCase(chatRepository)
    }

    @Singleton
    @Provides
    fun providesGetPartnerChatRoomStateFlowUseCase(chatRepository: ChatRepository): GetPartnerChatRoomStateFlowUseCase {
        return GetPartnerChatRoomStateFlowUseCase(chatRepository)
    }

    @Singleton
    @Provides
    fun providesPreloadImageUseCase(chatRepository: ChatRepository): PreloadImageUseCase {
        return PreloadImageUseCase(chatRepository)
    }


    /** Question **/

    @Singleton
    @Provides
    fun providesGetTodayQuestionUseCase(tokenRepository: TokenRepository, questionRepository: QuestionRepository): GetTodayQuestionUseCase {
        return GetTodayQuestionUseCase(tokenRepository, questionRepository)
    }

    @Singleton
    @Provides
    fun providesChangeTodayQuestionCacheUseCase(questionRepository: QuestionRepository): ChangeTodayQuestionCacheUseCase {
        return ChangeTodayQuestionCacheUseCase(questionRepository)
    }

    @Singleton
    @Provides
    fun providesGetQuestionUseCase(tokenRepository: TokenRepository, questionRepository: QuestionRepository): GetQuestionUseCase {
        return GetQuestionUseCase(tokenRepository, questionRepository)
    }

    @Singleton
    @Provides
    fun providesGetPagingQuestionUseCase(questionRepository: QuestionRepository): GetPagingQuestionUseCase {
        return GetPagingQuestionUseCase(questionRepository)
    }

    @Singleton
    @Provides
    fun providesAnswerQuestionUseCase(tokenRepository: TokenRepository, questionRepository: QuestionRepository): AnswerQuestionUseCase {
        return AnswerQuestionUseCase(tokenRepository, questionRepository)
    }

    @Singleton
    @Provides
    fun providesPressForAnswerUseCase(tokenRepository: TokenRepository, questionRepository: QuestionRepository): PressForAnswerUseCase {
        return PressForAnswerUseCase(tokenRepository, questionRepository)
    }

    @Singleton
    @Provides
    fun providesShareQuestionUseCase(tokenRepository: TokenRepository, questionRepository: QuestionRepository): ShareQuestionUseCase {
        return ShareQuestionUseCase(tokenRepository, questionRepository)
    }

    @Singleton
    @Provides
    fun providesAnswerPartnerQuestionCacheUseCase(questionRepository: QuestionRepository): AnswerPartnerQuestionCacheUseCase {
        return AnswerPartnerQuestionCacheUseCase(questionRepository)
    }

    @Singleton
    @Provides
    fun providesGetAnswerQuestionFlowUseCase(questionRepository: QuestionRepository): GetAnswerQuestionFlowUseCase {
        return GetAnswerQuestionFlowUseCase(questionRepository)
    }

    @Singleton
    @Provides
    fun providesGetTodayQuestionFlowUseCase(questionRepository: QuestionRepository): GetTodayQuestionFlowUseCase {
        return GetTodayQuestionFlowUseCase(questionRepository)
    }

    @Singleton
    @Provides
    fun providesSetQuestionUpdatedUseCase(questionRepository: QuestionRepository): SetQuestionUpdatedUseCase {
        return SetQuestionUpdatedUseCase(questionRepository)
    }

    @Singleton
    @Provides
    fun providesGetQuestionUpdatedFlowUseCase(questionRepository: QuestionRepository): GetQuestionUpdatedFlowUseCase {
        return GetQuestionUpdatedFlowUseCase(questionRepository)
    }


    /** Challenge **/

    @Singleton
    @Provides
    fun providesGetPagingOngoingChallengeUseCase(challengeRepository: ChallengeRepository): GetPagingOngoingChallengeUseCase {
        return GetPagingOngoingChallengeUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesGetPagingCompletedChallengeUseCase(challengeRepository: ChallengeRepository): GetPagingCompletedChallengeUseCase {
        return GetPagingCompletedChallengeUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesAddChallengeUseCase(tokenRepository: TokenRepository, challengeRepository: ChallengeRepository): AddMyChallengeUseCase {
        return AddMyChallengeUseCase(tokenRepository, challengeRepository)
    }

    @Singleton
    @Provides
    fun providesModifyChallengeUseCase(tokenRepository: TokenRepository, challengeRepository: ChallengeRepository): ModifyChallengeUseCase {
        return ModifyChallengeUseCase(tokenRepository, challengeRepository)
    }

    @Singleton
    @Provides
    fun providesDeleteChallengeUseCase(tokenRepository: TokenRepository, challengeRepository: ChallengeRepository): DeleteChallengeUseCase {
        return DeleteChallengeUseCase(tokenRepository, challengeRepository)
    }

    @Singleton
    @Provides
    fun providesCompleteChallengeUseCase(tokenRepository: TokenRepository, challengeRepository: ChallengeRepository): CompleteChallengeUseCase {
        return CompleteChallengeUseCase(tokenRepository, challengeRepository)
    }

    @Singleton
    @Provides
    fun providesGetChallengeUseCase(tokenRepository: TokenRepository, challengeRepository: ChallengeRepository): GetChallengeUseCase {
        return GetChallengeUseCase(tokenRepository, challengeRepository)
    }

    @Singleton
    @Provides
    fun providesGetChallengeCountUseCase(tokenRepository: TokenRepository, challengeRepository: ChallengeRepository): GetChallengeCountUseCase {
        return GetChallengeCountUseCase(tokenRepository, challengeRepository)
    }

    @Singleton
    @Provides
    fun providesAddPartnerChallengeCacheUseCase(challengeRepository: ChallengeRepository): AddPartnerChallengeCacheUseCase {
        return AddPartnerChallengeCacheUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesGetAddChallengeFlowUseCase(challengeRepository: ChallengeRepository): GetAddChallengeFlowUseCase {
        return GetAddChallengeFlowUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesDeletePartnerChallengeCacheUseCase(challengeRepository: ChallengeRepository): DeletePartnerChallengeCacheUseCase {
        return DeletePartnerChallengeCacheUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesGetDeleteChallengeFlowUseCase(challengeRepository: ChallengeRepository): GetDeleteChallengeFlowUseCase {
        return GetDeleteChallengeFlowUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesModifyPartnerChallengeCacheUseCase(challengeRepository: ChallengeRepository): ModifyPartnerChallengeCacheUseCase {
        return ModifyPartnerChallengeCacheUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesGetModifyChallengeFlowUseCase(challengeRepository: ChallengeRepository): GetModifyChallengeFlowUseCase {
        return GetModifyChallengeFlowUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesSetChallengeUpdatedUseCase(challengeRepository: ChallengeRepository): SetChallengeUpdatedUseCase {
        return SetChallengeUpdatedUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesGetChallengeUpdatedFlowUseCase(challengeRepository: ChallengeRepository): GetChallengeUpdatedFlowUseCase {
        return GetChallengeUpdatedFlowUseCase(challengeRepository)
    }

    @Singleton
    @Provides
    fun providesShareChallengeUseCase(tokenRepository: TokenRepository, challengeRepository: ChallengeRepository): ShareChallengeUseCase {
        return ShareChallengeUseCase(tokenRepository, challengeRepository)
    }


    /** Signal **/

    @Singleton
    @Provides
    fun providesGetMySignalUseCase(tokenRepository: TokenRepository, signalRepository: SignalRepository): GetMySignalUseCase {
        return GetMySignalUseCase(tokenRepository, signalRepository)
    }

    @Singleton
    @Provides
    fun providesGetPartnerSignalUseCase(tokenRepository: TokenRepository, signalRepository: SignalRepository): GetPartnerSignalUseCase {
        return GetPartnerSignalUseCase(tokenRepository, signalRepository)
    }

    @Singleton
    @Provides
    fun providesChangeMySignalUseCase(tokenRepository: TokenRepository, signalRepository: SignalRepository): ChangeMySignalUseCase {
        return ChangeMySignalUseCase(tokenRepository, signalRepository)
    }

    @Singleton
    @Provides
    fun providesChangePartnerSignalCacheUseCase(signalRepository: SignalRepository): ChangePartnerSignalCacheUseCase {
        return ChangePartnerSignalCacheUseCase(signalRepository)
    }

    @Singleton
    @Provides
    fun providesGetPartnerSignalFlowUseCase(signalRepository: SignalRepository): GetPartnerSignalFlowUseCase {
        return GetPartnerSignalFlowUseCase(signalRepository)
    }

    @Singleton
    @Provides
    fun providesGetMySignalCacheFlowUseCase(signalRepository: SignalRepository): GetMySignalCacheFlowUseCase {
        return GetMySignalCacheFlowUseCase(signalRepository)
    }








    /** Old **/



    @Singleton
    @Provides
    fun providesGetChatListUseCase2(userRepository: UserRepository, chatRepository2: ChatRepository2): GetChatListUseCase2 {
        return GetChatListUseCase2(userRepository, chatRepository2)
    }

    @Singleton
    @Provides
    fun providesSendChatUseCase(userRepository: UserRepository, chatRepository2: ChatRepository2): SendChatUseCase {
        return SendChatUseCase(userRepository, chatRepository2)
    }

    @Singleton
    @Provides
    fun providesGetNewsListUseCase(userRepository: UserRepository, newsRepository: NewsRepository): GetNewsListUseCase {
        return GetNewsListUseCase(userRepository, newsRepository)
    }

    @Singleton
    @Provides
    fun providesGetQuestionListUseCase(userRepository: UserRepository, questionRepository2: QuestionRepository2): GetQuestionListUseCase {
        return GetQuestionListUseCase(userRepository, questionRepository2)
    }

    @Singleton
    @Provides
    fun providesGetTodayQuestionAnswersFromServer(userRepository: UserRepository, questionRepository2: QuestionRepository2): GetTodayQuestionAnswersFromServer {
        return GetTodayQuestionAnswersFromServer(userRepository, questionRepository2)
    }

    @Singleton
    @Provides
    fun providesSendQuestionAnswerUseCase(userRepository: UserRepository, questionRepository2: QuestionRepository2, chatRepository2: ChatRepository2): SendQuestionAnswerUseCase {
        return SendQuestionAnswerUseCase(userRepository, questionRepository2, chatRepository2)
    }

    @Singleton
    @Provides
    fun providesSaveChatUseCase(chatRepository2: ChatRepository2): SaveChatUseCase {
        return SaveChatUseCase(chatRepository2)
    }

    @Singleton
    @Provides
    fun providesAutoLogInWithGoogleUseCase(userRepository: UserRepository): AutoLogInWithGoogleUseCase {
        return AutoLogInWithGoogleUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesSignUpWithGoogleUseCase(userRepository: UserRepository): SignUpWithGoogleUseCase {
        return SignUpWithGoogleUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesSignUpWithKakaoUseCase(userRepository: UserRepository): SignUpWithKakaoUseCase {
        return SignUpWithKakaoUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetCoupleRegistrationCode(userRepository: UserRepository): GetCoupleRegistrationCodeUseCase {
        return GetCoupleRegistrationCodeUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesSendPartnerCoupleRegistrationCodeUseCase(userRepository: UserRepository): SendPartnerCoupleRegistrationCodeUseCase {
        return SendPartnerCoupleRegistrationCodeUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesRemoveCoupleRegistrationCodeUseCase(userRepository: UserRepository): RemoveCoupleRegistrationCodeUseCase {
        return RemoveCoupleRegistrationCodeUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetUserInfoUseCase(userRepository: UserRepository): GetUserInfoUseCase {
        return GetUserInfoUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesCheckUserIsCoupleUseCase(userRepository: UserRepository): CheckUserIsCoupleUseCase {
        return CheckUserIsCoupleUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesUploadMyPublicKeyUseCase(userRepository: UserRepository, encryptionRepository: EncryptionRepository): UploadMyPublicKeyUseCase {
        return UploadMyPublicKeyUseCase(userRepository, encryptionRepository)
    }

    @Singleton
    @Provides
    fun providesLoadPartnerPublicKeyUseCase(userRepository: UserRepository, encryptionRepository: EncryptionRepository): LoadPartnerPublicKeyUseCase {
        return LoadPartnerPublicKeyUseCase(userRepository, encryptionRepository)
    }

    @Singleton
    @Provides
    fun providesUploadMyPrivateKeyUseCase(userRepository: UserRepository, encryptionRepository: EncryptionRepository): UploadMyPrivateKeyUseCase {
        return UploadMyPrivateKeyUseCase(userRepository, encryptionRepository)
    }

    @Singleton
    @Provides
    fun providesLoadPartnerPrivateKeyUseCase(userRepository: UserRepository, encryptionRepository: EncryptionRepository): LoadPartnerPrivateKeyUseCase {
        return LoadPartnerPrivateKeyUseCase(userRepository, encryptionRepository)
    }

    @Singleton
    @Provides
    fun providesCheckUserInfoIsEnteredUseCase(userRepository: UserRepository): CheckUserInfoIsEnteredUseCase {
        return CheckUserInfoIsEnteredUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesUpdateUserInfoUseCase(userRepository: UserRepository): UpdateUserInfoUseCase {
        return UpdateUserInfoUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetTodayQuestionUseCase2(questionRepository2: QuestionRepository2, userRepository: UserRepository): GetTodayQuestionUseCase2 {
        return GetTodayQuestionUseCase2(questionRepository2, userRepository)
    }

    @Singleton
    @Provides
    fun providesUpdateMyEmotionUseCase(userRepository: UserRepository): UpdateMyEmotionUseCase {
        return UpdateMyEmotionUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetPartnerInfo2UseCase(userRepository: UserRepository): GetPartnerInfo2UseCase {
        return GetPartnerInfo2UseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetCoupleAnniversaryUseCase(userRepository: UserRepository): GetCoupleAnniversaryUseCase {
        return GetCoupleAnniversaryUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesBreakUpCoupleUseCase2(userRepository: UserRepository): BreakUpCoupleUseCase2 {
        return BreakUpCoupleUseCase2(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetQuestionByContentFromDataBaseUseCase(questionRepository2: QuestionRepository2): GetQuestionByContentFromDataBaseUseCase {
        return GetQuestionByContentFromDataBaseUseCase(questionRepository2)
    }

    @Singleton
    @Provides
    fun providesClearAllExceptKeysUseCase(userRepository: UserRepository): ClearAllExceptKeysUseCase {
        return ClearAllExceptKeysUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesAutoLogInWithKakaoUseCase(userRepository: UserRepository): AutoLogInWithKakaoUseCase {
        return AutoLogInWithKakaoUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesSignUpWithNaverUseCase(userRepository: UserRepository): SignUpWithNaverUseCase {
        return SignUpWithNaverUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesAutoLogInWithNaverUseCase(userRepository: UserRepository): AutoLogInWithNaverUseCase {
        return AutoLogInWithNaverUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesRequestChangingPartnerEmotionUseCase(userRepository: UserRepository): RequestChangingPartnerEmotionUseCase {
        return RequestChangingPartnerEmotionUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesCheckUserIsSignedUpUseCase(userRepository: UserRepository): CheckUserIsSignedUpUseCase {
        return CheckUserIsSignedUpUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesSaveQuestionToDatabaseUseCase(questionRepository2: QuestionRepository2): SaveQuestionToDatabaseUseCase {
        return SaveQuestionToDatabaseUseCase(questionRepository2)
    }

    @Singleton
    @Provides
    fun providesClearCoupleInfoUseCase(userRepository: UserRepository): ClearCoupleInfoUseCase {
        return ClearCoupleInfoUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetMyProfileImageUrlUseCase(userRepository: UserRepository): GetMyProfileImageUrlUseCase {
        return GetMyProfileImageUrlUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetPartnerProfileImageUrlUseCase(userRepository: UserRepository): GetPartnerProfileImageUrlUseCase {
        return GetPartnerProfileImageUrlUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesSignUpWithAppleUseCase(userRepository: UserRepository): SignUpWithAppleUseCase {
        return SignUpWithAppleUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesAutoLogInWithAppleUseCase(userRepository: UserRepository): AutoLogInWithAppleUseCase {
        return AutoLogInWithAppleUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesCheckIsAppleLoggedInUseCase(userRepository: UserRepository): CheckIsAppleLoggedInUseCase {
        return CheckIsAppleLoggedInUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesUpdateProfileImageUseCase(userRepository: UserRepository): UpdateProfileImageUseCase {
        return UpdateProfileImageUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesUpdateNicknameUseCase(userRepository: UserRepository): UpdateNicknameUseCase {
        return UpdateNicknameUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesUpdateBirthUseCase(userRepository: UserRepository): UpdateBirthUseCase {
        return UpdateBirthUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesUpdateCoupleAnniversaryUseCase(userRepository: UserRepository): UpdateCoupleAnniversaryUseCase {
        return UpdateCoupleAnniversaryUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesReloadChatListUseCase(userRepository: UserRepository, chatRepository2: ChatRepository2): ReloadChatListUseCase {
        return ReloadChatListUseCase(userRepository, chatRepository2)
    }

    @Singleton
    @Provides
    fun providesGetQuestionAnswersUseCase(userRepository: UserRepository, questionRepository2: QuestionRepository2): GetQuestionAnswersUseCase {
        return GetQuestionAnswersUseCase(userRepository, questionRepository2)
    }

    @Singleton
    @Provides
    fun providesRestoreKeysUseCase(userRepository: UserRepository, encryptionRepository: EncryptionRepository): RestoreKeysUseCase {
        return RestoreKeysUseCase(userRepository, encryptionRepository)
    }

    @Singleton
    @Provides
    fun providesHelpToRestoreKeysUseCase(userRepository: UserRepository, encryptionRepository: EncryptionRepository): HelpToRestoreKeysUseCase {
        return HelpToRestoreKeysUseCase(userRepository, encryptionRepository)
    }

    @Singleton
    @Provides
    fun providesRequestToRestoreKeysUseCase(userRepository: UserRepository, encryptionRepository: EncryptionRepository): RequestToRestoreKeysUseCase {
        return RequestToRestoreKeysUseCase(userRepository, encryptionRepository)
    }

    @Singleton
    @Provides
    fun providesCheckKeyPairsExistUseCase(encryptionRepository: EncryptionRepository): CheckKeyPairsExistUseCase {
        return CheckKeyPairsExistUseCase(encryptionRepository)
    }

    @Singleton
    @Provides
    fun providesCheckKeyPairsWorkWellUseCase(encryptionRepository: EncryptionRepository): CheckKeyPairsWorkWellUseCase {
        return CheckKeyPairsWorkWellUseCase(encryptionRepository)
    }

    @Singleton
    @Provides
    fun providesGetPartnerInfoFlow2UseCase(userRepository: UserRepository): GetPartnerInfoFlow2UseCase {
        return GetPartnerInfoFlow2UseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetMixpanelAPIUseCase(@ApplicationContext context: Context): GetMixpanelAPIUseCase {
        return GetMixpanelAPIUseCase(context)
    }

    @Singleton
    @Provides
    fun providesGetUserIsActiveUseCase(@Named("AppSettings") pref: SharedPreferences): GetUserIsActiveUseCase {
        return GetUserIsActiveUseCase(pref)
    }

    @Singleton
    @Provides
    fun providesSetUserIsActiveUseCase(@Named("AppSettings") pref: SharedPreferences): SetUserIsActiveUseCase {
        return SetUserIsActiveUseCase(pref)
    }

    @Singleton
    @Provides
    fun providesGetQuestionDetailUseCase(userRepository: UserRepository, questionRepository2: QuestionRepository2): GetQuestionDetailUseCase {
        return GetQuestionDetailUseCase(userRepository, questionRepository2)
    }

    @Singleton
    @Provides
    fun providesLeaveFeeltalkUseCase(userRepository: UserRepository): LeaveFeeltalkUseCase {
        return LeaveFeeltalkUseCase(userRepository)
    }
}