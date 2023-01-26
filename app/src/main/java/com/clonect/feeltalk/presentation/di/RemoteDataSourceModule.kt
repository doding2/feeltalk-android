package com.clonect.feeltalk.presentation.di

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.user.datasource.UserRemoteDataSource
import com.clonect.feeltalk.data.repository.user.datasourceImpl.UserRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDataSourceModule {

    @Singleton
    @Provides
    fun providesUserRemoteDataSource(
        clonectService: ClonectService
    ): UserRemoteDataSource {
        return UserRemoteDataSourceImpl(clonectService)
    }

}