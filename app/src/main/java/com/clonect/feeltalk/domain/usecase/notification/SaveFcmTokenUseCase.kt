package com.clonect.feeltalk.domain.usecase.notification

import android.content.SharedPreferences
import android.util.Log
import com.clonect.feeltalk.presentation.utils.infoLog

class SaveFcmTokenUseCase(
    private val fcmPref: SharedPreferences
) {
    operator fun invoke(fcmToken: String) {
        fcmPref.edit()
            .putString("FcmToken", fcmToken)
            .apply()

        infoLog("fcmToken: $fcmToken")
    }
}