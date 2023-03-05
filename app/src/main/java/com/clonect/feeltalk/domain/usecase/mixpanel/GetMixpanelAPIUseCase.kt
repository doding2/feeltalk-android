package com.clonect.feeltalk.domain.usecase.mixpanel

import android.content.Context
import com.clonect.feeltalk.BuildConfig
import com.mixpanel.android.mpmetrics.MixpanelAPI

class GetMixpanelAPIUseCase(
    private val context: Context
) {
    operator fun invoke(): MixpanelAPI {
        val token = if (BuildConfig.DEBUG) BuildConfig.MIXPANEL_DEBUG_TOKEN else BuildConfig.MIXPANEL_RELEASE_TOKEN
        return MixpanelAPI.getInstance(context, token, true)
    }
}