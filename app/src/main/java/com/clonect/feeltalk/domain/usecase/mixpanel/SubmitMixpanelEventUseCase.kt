package com.clonect.feeltalk.domain.usecase.mixpanel

import com.clonect.feeltalk.BuildConfig
import com.google.gson.JsonObject
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

class SubmitMixpanelEventUseCase(
    private val mixpanelAPI: MixpanelAPI
) {
    operator fun invoke(eventName: String, event: JsonObject) {
        if (!BuildConfig.DEBUG) {
            mixpanelAPI.track(eventName, JSONObject(event.toString()))
        }
    }
}