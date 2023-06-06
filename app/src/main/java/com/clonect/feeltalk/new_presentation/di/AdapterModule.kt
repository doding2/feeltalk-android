package com.clonect.feeltalk.new_presentation.di

import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.completed.CompletedChallengeAdapter
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoing.OngoingChallengeAdapter
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.question.QuestionAdapter
import com.clonect.feeltalk.presentation.ui.chat.ChatAdapter
import com.clonect.feeltalk.presentation.ui.guide.GuideAdapter
import com.clonect.feeltalk.presentation.ui.news.NewsAdapter
import com.clonect.feeltalk.presentation.ui.question_list.QuestionListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdapterModule {

    @Singleton
    @Provides
    fun providesQuestionAdapter(): QuestionAdapter {
        return QuestionAdapter()
    }

    @Singleton
    @Provides
    fun providesOngoingChallengeAdapter(): OngoingChallengeAdapter {
        return OngoingChallengeAdapter()
    }

    @Singleton
    @Provides
    fun providesCompletedChallengeAdapter(): CompletedChallengeAdapter {
        return CompletedChallengeAdapter()
    }



    // 밑에 것들은 나중에 지워야 됨

    @Singleton
    @Provides
    fun providesQuestionListAdapter(): QuestionListAdapter {
        return QuestionListAdapter()
    }

    @Provides
    fun providesChatAdapter(): ChatAdapter {
        return ChatAdapter()
    }

    @Singleton
    @Provides
    fun providesNewsAdapter(): NewsAdapter {
        return NewsAdapter()
    }

    @Singleton
    @Provides
    fun providesGuideAdapter(): GuideAdapter {
        return GuideAdapter(emptyList())
    }
}