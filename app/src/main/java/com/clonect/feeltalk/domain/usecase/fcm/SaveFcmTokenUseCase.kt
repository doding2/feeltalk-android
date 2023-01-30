package com.clonect.feeltalk.domain.usecase.fcm

import android.content.SharedPreferences
import android.util.Log

class SaveFcmTokenUseCase(
    private val fcmPref: SharedPreferences
) {
    operator fun invoke(fcmToken: String) {
        fcmPref.edit()
            .putString("FcmToken", fcmToken)
            .apply()

        Log.i("SaveFcmTokenUseCase", "fcmToken: $fcmToken")
    }
}