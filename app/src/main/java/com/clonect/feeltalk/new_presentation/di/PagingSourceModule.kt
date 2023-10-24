package com.clonect.feeltalk.new_presentation.di

import com.clonect.feeltalk.new_data.repository.challenge.dataSource.ChallengeRemoteDataSource
import com.clonect.feeltalk.new_data.repository.challenge.paging.CompletedChallengePagingSource
import com.clonect.feeltalk.new_data.repository.challenge.paging.OngoingChallengePagingSource
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_data.repository.chat.paging.ChatPagingSource
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionRemoteDataSource
import com.clonect.feeltalk.new_data.repository.question.paging.QuestionPagingSource
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository
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
        questionRepository: QuestionRepository,
        challengeRepository: ChallengeRepository,
        chatRemoteDataSource: ChatRemoteDataSource
    ): ChatPagingSource {
        return ChatPagingSource(tokenRepository, questionRepository, challengeRepository, chatRemoteDataSource)
    }

    @Provides
    fun providesQuestionPagingSource(
        tokenRepository: TokenRepository,
        questionRemoteDataSource: QuestionRemoteDataSource
    ): QuestionPagingSource {
        return QuestionPagingSource(tokenRepository, questionRemoteDataSource)
    }

    @Provides
    fun providesOngoingChallengePagingSource(
        tokenRepository: TokenRepository,
        challengeRemoteDataSource: ChallengeRemoteDataSource,
    ): OngoingChallengePagingSource {
        return OngoingChallengePagingSource(tokenRepository, challengeRemoteDataSource)
    }

    @Provides
    fun providesCompletedChallengePagingSource(
        tokenRepository: TokenRepository,
        challengeRemoteDataSource: ChallengeRemoteDataSource,
    ): CompletedChallengePagingSource {
        return CompletedChallengePagingSource(tokenRepository, challengeRemoteDataSource)
    }

}