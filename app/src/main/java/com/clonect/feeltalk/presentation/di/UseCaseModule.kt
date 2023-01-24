package com.clonect.feeltalk.presentation.di

import com.clonect.feeltalk.domain.repository.UserRepository
import com.clonect.feeltalk.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
}