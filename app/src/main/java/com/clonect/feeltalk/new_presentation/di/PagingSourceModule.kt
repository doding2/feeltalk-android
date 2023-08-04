package com.clonect.feeltalk.new_presentation.di

import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_data.repository.chat.paging.ChatPagingSource
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionRemoteDataSource
import com.clonect.feeltalk.new_data.repository.question.paging.QuestionPagingSource
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PagingSourceModule {

    @Provides
    fun providesChatPagingSource(
        tokenRepository: TokenRepository,
        chatRemoteDataSource: ChatRemoteDataSource
    ): ChatPagingSource {
        return ChatPagingSource(tokenRepository, chatRemoteDataSource)
    }

    @Provides
    fun providesQuestionPagingSource(
        tokenRepository: TokenRepository,
        questionRemoteDataSource: QuestionRemoteDataSource
    ): QuestionPagingSource {
        return QuestionPagingSource(tokenRepository, questionRemoteDataSource)
    }

}