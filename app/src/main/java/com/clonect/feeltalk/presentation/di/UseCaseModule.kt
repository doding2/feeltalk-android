package com.clonect.feeltalk.presentation.di

import android.content.SharedPreferences
import com.clonect.feeltalk.data.repository.notification.NotificationRepository
import com.clonect.feeltalk.domain.repository.ChatRepository
import com.clonect.feeltalk.domain.repository.EncryptionRepository
import com.clonect.feeltalk.domain.repository.QuestionRepository
import com.clonect.feeltalk.domain.repository.UserRepository
import com.clonect.feeltalk.domain.usecase.*
import com.clonect.feeltalk.domain.usecase.chat.GetChatListUseCase
import com.clonect.feeltalk.domain.usecase.chat.SaveChatUseCase
import com.clonect.feeltalk.domain.usecase.chat.SendChatUseCase
import com.clonect.feeltalk.domain.usecase.emotion.GetPartnerEmotionUseCase
import com.clonect.feeltalk.domain.usecase.encryption.*
import com.clonect.feeltalk.domain.usecase.notification.GetFcmTokenUseCase
import com.clonect.feeltalk.domain.usecase.notification.SaveFcmTokenUseCase
import com.clonect.feeltalk.domain.usecase.notification.SendFcmUseCase
import com.clonect.feeltalk.domain.usecase.news.GetNewsListUseCase
import com.clonect.feeltalk.domain.usecase.question.GetQuestionByIdUseCase
import com.clonect.feeltalk.domain.usecase.question.GetQuestionListUseCase
import com.clonect.feeltalk.domain.usecase.question.GetTodayQuestionUseCase
import com.clonect.feeltalk.domain.usecase.user.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun providesGetChatListUseCase(chatRepository: ChatRepository): GetChatListUseCase {
        return GetChatListUseCase(chatRepository)
    }

    @Singleton
    @Provides
    fun providesSendChatUseCase(chatRepository: ChatRepository): SendChatUseCase {
        return SendChatUseCase(chatRepository)
    }

    @Singleton
    @Provides
    fun providesGetNewsListUseCase(): GetNewsListUseCase {
        return GetNewsListUseCase()
    }

    @Singleton
    @Provides
    fun providesGetPartnerEmotionUseCase(): GetPartnerEmotionUseCase {
        return GetPartnerEmotionUseCase()
    }

    @Singleton
    @Provides
    fun providesGetQuestionListUseCase(): GetQuestionListUseCase {
        return GetQuestionListUseCase()
    }

    @Singleton
    @Provides
    fun providesSaveFcmTokenUseCase(@Named("FcmToken") fcmPref: SharedPreferences): SaveFcmTokenUseCase {
        return SaveFcmTokenUseCase(fcmPref)
    }

    @Singleton
    @Provides
    fun providesGetFcmTokenUseCase(@Named("FcmToken") fcmPref: SharedPreferences): GetFcmTokenUseCase {
        return GetFcmTokenUseCase(fcmPref)
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
    fun providesGetQuestionByIdUseCase(): GetQuestionByIdUseCase {
        return GetQuestionByIdUseCase()
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
    fun providesTestUseCase(encryptionRepository: EncryptionRepository): TestUseCase {
        return TestUseCase(encryptionRepository)
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
}