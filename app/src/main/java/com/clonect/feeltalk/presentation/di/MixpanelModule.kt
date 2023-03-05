package com.clonect.feeltalk.presentation.di

import android.content.Context
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.domain.usecase.mixpanel.SubmitMixpanelEventUseCase
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MixpanelModule {

    @Provides
    @Singleton
    fun providesMixpanelAPI(@ApplicationContext context: Context): MixpanelAPI {
        val token = if (BuildConfig.DEBUG) BuildConfig.MIXPANEL_DEBUG_TOKEN else BuildConfig.MIXPANEL_RELEASE_TOKEN
        return MixpanelAPI.getInstance(context, token, true)
    }

    @Singleton
    @Provides
    fun providesSubmitMixpanelEventUseCase(mixpanelAPI: MixpanelAPI): SubmitMixpanelEventUseCase {
        return SubmitMixpanelEventUseCase(mixpanelAPI)
    }

}