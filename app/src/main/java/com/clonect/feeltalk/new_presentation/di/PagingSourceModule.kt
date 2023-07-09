package com.clonect.feeltalk.new_presentation.di

import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatCacheDataSource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_data.repository.paging.ChatPagingSource
import com.clonect.feeltalk.new_domain.repository.signIn.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class PagingSourceModule {

    @ViewModelScoped
    @Provides
    fun providesChatPagingSource(
        tokenRepository: TokenRepository,
        chatCacheDataSource: ChatCacheDataSource,
        chatRemoteDataSource: ChatRemoteDataSource
    ): ChatPagingSource {
        return ChatPagingSource(tokenRepository, chatCacheDataSource, chatRemoteDataSource)
    }

}