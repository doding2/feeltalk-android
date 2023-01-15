package com.clonect.feeltalk.presentation.di

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
    fun providesQuestionListAdapter(): QuestionListAdapter {
        return QuestionListAdapter()
    }

}