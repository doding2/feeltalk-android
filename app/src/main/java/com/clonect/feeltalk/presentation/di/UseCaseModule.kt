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
    fun providesGetGoogleTokensUseCase(userRepository: UserRepository): GetGoogleTokensUseCase {
        return GetGoogleTokensUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesSignUpWithEmailUseCase(userRepository: UserRepository): SignUpWithEmailUseCase {
        return SignUpWithEmailUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesLogInWithEmailUseCase(userRepository: UserRepository): LogInWithEmailUseCase {
        return LogInWithEmailUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetUserInfoUseCase(userRepository: UserRepository): GetUserInfoUseCase {
        return GetUserInfoUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun providesGetChatListUseCase(): GetChatListUseCase {
        return GetChatListUseCase()
    }
}