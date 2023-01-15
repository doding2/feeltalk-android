package com.clonect.feeltalk.presentation.di

import com.clonect.feeltalk.domain.repository.UserRepository
import com.clonect.feeltalk.domain.usecase.LogInWithGoogle
import com.clonect.feeltalk.domain.usecase.SignUpWithEmail
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
    fun providesLogInWithGoogle(userRepository: UserRepository): LogInWithGoogle {
        return LogInWithGoogle(userRepository)
    }

    @Singleton
    @Provides
    fun providesSignUpWithEmail(userRepository: UserRepository): SignUpWithEmail {
        return SignUpWithEmail(userRepository)
    }

}