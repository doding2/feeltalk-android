package com.clonect.feeltalk.presentation.di

import android.content.SharedPreferences
import com.clonect.feeltalk.data.repository.notification.NotificationRepository
import com.clonect.feeltalk.domain.usecase.*
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
    fun providesGetChatListUseCase(): GetChatListUseCase {
        return GetChatListUseCase()
    }

    @Singleton
    @Provides
    fun providesGetNewsListUseCase(): GetNewsListUseCase {
        return GetNewsListUseCase()
    }

    @Singleton
    @Provides
    fun providesGetMyEmotionUseCase(): GetMyEmotionUseCase {
        return GetMyEmotionUseCase()
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

}