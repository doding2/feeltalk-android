package com.clonect.feeltalk.presentation.di

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.api.GoogleAuthService
import com.clonect.feeltalk.data.repository.user.UserRepositoryImpl
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
        clonectService: ClonectService,
        googleAuthService: GoogleAuthService
    ): UserRepository {
        return UserRepositoryImpl(clonectService, googleAuthService)
    }

}