package com.clonect.feeltalk.presentation.di

import com.clonect.feeltalk.data.api.NotificationService
import com.clonect.feeltalk.data.repository.notification.NotificationRepository
import com.clonect.feeltalk.data.repository.user.UserRepositoryImpl
import com.clonect.feeltalk.data.repository.user.datasource.UserCacheDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun providesUserRepository(
        remoteDataSource: UserRemoteDataSource,
        localDataSource: UserLocalDataSource,
        cacheDataSource: UserCacheDataSource
    ): UserRepository {
        return UserRepositoryImpl(remoteDataSource, localDataSource, cacheDataSource)
    }

    @Singleton
    @Provides
    fun providesNotificationRepository(
        notificationService: NotificationService
    ): NotificationRepository {
        return NotificationRepository(notificationService)
    }

}