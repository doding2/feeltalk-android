package com.clonect.feeltalk.common

import java.util.*

fun Date.plusDayBy(day: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DATE, day)
    return cal.time
}

fun Date.plusSecondsBy(seconds: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.SECOND, seconds)
    return cal.time
}

fun Date.plusHoursBy(hours: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.HOUR, hours)
    return cal.time
}