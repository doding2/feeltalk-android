package com.clonect.feeltalk.new_presentation.ui.util

import android.app.Activity
import android.content.res.Resources
import android.util.DisplayMetrics

fun Activity?.dpToPx(dp: Float): Float {
    val resources: Resources = this?.applicationContext?.resources ?: return 0f
    val metrics: DisplayMetrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Activity?.pxToDp(px: Float): Float {
    val resources = this?.applicationContext?.resources ?: return 0f
    val metrics = resources.displayMetrics
    return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}