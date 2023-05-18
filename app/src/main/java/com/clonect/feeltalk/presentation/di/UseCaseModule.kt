package com.clonect.feeltalk.presentation.di

import android.content.Context
import android.content.SharedPreferences
import com.clonect.feeltalk.data.repository.notification.NotificationRepository
import com.clonect.feeltalk.data.utils.AppLevelEncryptHelper
import com.clonect.feeltalk.domain.repository.*
import com.clonect.feeltalk.domain.usecase.*
import com.clonect.feeltalk.domain.usecase.app_settings.GetAppSettingsUseCase
import com.clonect.feeltalk.domain.usecase.app_settings.SaveAppSettingsUseCase
import com.clonect.feeltalk.domain.usecase.chat.GetChatListUseCase
import com.clonect.feeltalk.domain.usecase.chat.ReloadChatListUseCase
import com.clonect.feeltalk.domain.usecase.chat.SaveChatUseCase
import com.clonect.feeltalk.domain.usecase.chat.SendChatUseCase
import com.clonect.feeltalk.domain.usecase.encryption.*
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.domain.usecase.news.GetNewsListUseCase
import com.clonect.feeltalk.domain.usecase.notification.SendFcmUseCase
import com.clonect.feeltalk.domain.usecase.question.*
import com.clonect.feeltalk.domain.usecase.user.*
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
        userRepository: UserRepository
    ): SaveAppSettingsUseCase {
        return SaveAppSettingsUseCase(pref, appLevelEncryptHelper, userRepository)
    }


    @Singleton
    @Provides
    fun providesGetChatListUseCase(userRepository: UserRepository, chatRepository: ChatRepository): GetChatListUseCase {
        return GetChatListUseCase(userRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesSendChatUseCase(userRepository: UserRepository, chatRepository: ChatRepository): SendChatUseCase {
        return SendChatUseCase(userRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesGetNewsListUseCase(userRepository: UserRepository, newsRepository: NewsRepository): GetNewsListUseCase {
        return GetNewsListUseCase(userRepository, newsRepository)
    }

    @Singleton
    @Provides
    fun providesGetQuestionListUseCase(userRepository: UserRepository, questionRepository: QuestionRepository): GetQuestionListUseCase {
        return GetQuestionListUseCase(userRepository, questionRepository)
    }

    @Singleton
    @Provides
    fun providesGetTodayQuestionAnswersFromServer(userRepository: UserRepository, questionRepository: QuestionRepository): GetTodayQuestionAnswersFromServer {
        return GetTodayQuestionAnswersFromServer(userRepository, questionRepository)
    }

    @Singleton
    @Provides
    fun providesSendQuestionAnswerUseCase(userRepository: UserRepository, questionRepository: QuestionRepository, chatRepository: ChatRepository): SendQuestionAnswerUseCase {
        return SendQuestionAnswerUseCase(userRepository, questionRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesSendFcmUseCase(notificationRepository: NotificationRepository): SendFcmUseCase {
        return SendFcmUseCase(notificationRepository)
    }

    @Singleton
    @Provides
    fun providesSaveChatUseCase(chatRepository: ChatRepository): SaveChatUseCase {
        return SaveChatUseCase(chatRepository)
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
    fun providesGetTodayQuestionUseCase(questionRepository: QuestionRepository, userRepository: UserRepository): GetTodayQuestionUseCase {
        return GetTodayQuestionUseCase(questionRepository, userRepository)
    }

    @Singleton
    @Provides
    fun providesUpdateMyEmotionUseCase(userRepository: UserRepository): UpdateMyEmotionUseCase {
        return UpdateMyEmotionUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetPartnerInfoUseCase(userRepository: UserRepository): GetPartnerInfoUseCase {
        return GetPartnerInfoUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetCoupleAnniversaryUseCase(userRepository: UserRepository): GetCoupleAnniversaryUseCase {
        return GetCoupleAnniversaryUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesBreakUpCoupleUseCase(userRepository: UserRepository): BreakUpCoupleUseCase {
        return BreakUpCoupleUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetQuestionByContentFromDataBaseUseCase(questionRepository: QuestionRepository): GetQuestionByContentFromDataBaseUseCase {
        return GetQuestionByContentFromDataBaseUseCase(questionRepository)
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
    fun providesSaveQuestionToDatabaseUseCase(questionRepository: QuestionRepository): SaveQuestionToDatabaseUseCase {
        return SaveQuestionToDatabaseUseCase(questionRepository)
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
    fun providesReloadChatListUseCase(userRepository: UserRepository, chatRepository: ChatRepository): ReloadChatListUseCase {
        return ReloadChatListUseCase(userRepository, chatRepository)
    }

    @Singleton
    @Provides
    fun providesGetQuestionAnswersUseCase(userRepository: UserRepository, questionRepository: QuestionRepository): GetQuestionAnswersUseCase {
        return GetQuestionAnswersUseCase(userRepository, questionRepository)
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
    fun providesGetPartnerInfoFlowUseCase(userRepository: UserRepository): GetPartnerInfoFlowUseCase {
        return GetPartnerInfoFlowUseCase(userRepository)
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
    fun providesGetQuestionDetailUseCase(userRepository: UserRepository, questionRepository: QuestionRepository): GetQuestionDetailUseCase {
        return GetQuestionDetailUseCase(userRepository, questionRepository)
    }

    @Singleton
    @Provides
    fun providesAutoLogInUseCase(userRepository: UserRepository): AutoLogInUseCase {
        return AutoLogInUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesLeaveFeeltalkUseCase(userRepository: UserRepository): LeaveFeeltalkUseCase {
        return LeaveFeeltalkUseCase(userRepository)
    }
}