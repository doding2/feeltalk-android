package com.clonect.feeltalk.presentation.utils

import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment

fun Fragment.dpToPx(dp: Float): Float {
    val resources: Resources = this.resources
    val metrics: DisplayMetrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Fragment.pxToDp(px: Float): Float {
    val resources = this.resources
    val metrics = resources.displayMetrics
    return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}