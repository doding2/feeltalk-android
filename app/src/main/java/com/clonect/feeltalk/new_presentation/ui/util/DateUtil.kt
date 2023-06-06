package com.clonect.feeltalk.new_presentation.ui.util

import java.util.*

fun plusNowBy(day: Int): Date {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DATE, day)
    return cal.time
}