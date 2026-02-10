package com.clonect.feeltalk.mvp_domain.usecase.mixpanel

import android.content.Context
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.common.Constants
import com.mixpanel.android.mpmetrics.MixpanelAPI

class GetMixpanelAPIUseCase(
    private val context: Context
) {
    operator fun invoke(): MixpanelAPI {
        val token = if (BuildConfig.DEBUG) Constants.MIXPANEL_DEBUG_TOKEN else Constants.MIXPANEL_RELEASE_TOKEN
        return MixpanelAPI.getInstance(context, token, true)
    }
}