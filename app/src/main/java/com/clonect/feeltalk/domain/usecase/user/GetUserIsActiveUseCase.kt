package com.clonect.feeltalk.domain.usecase.user

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class GetUserIsActiveUseCase(
    private val pref: SharedPreferences,
) {
    operator fun invoke(): Boolean {
        val date = pref.getString("lastAnswerDate", null) ?: return false

        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val prev = format.parse(date) ?: return false
        val current = Date()

        val diff: Double = (current.time - prev.time).toDouble()
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return days < 1.0
    }
}