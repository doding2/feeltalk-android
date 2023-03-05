package com.clonect.feeltalk.domain.usecase.user

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class SetUserIsActiveUseCase(
    private val pref: SharedPreferences,
) {
    operator fun invoke() {
        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        pref.edit()
            .putString("lastAnswerDate", format.format(Date()))
            .apply()
    }
}