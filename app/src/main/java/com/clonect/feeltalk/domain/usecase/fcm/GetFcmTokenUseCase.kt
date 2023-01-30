package com.clonect.feeltalk.domain.usecase.fcm

import android.content.SharedPreferences

class GetFcmTokenUseCase(
    private val fcmPref: SharedPreferences
) {
    operator fun invoke(): String? {
        return fcmPref.getString("FcmToken", null)
    }
}