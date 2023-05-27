package com.clonect.feeltalk.new_presentation.ui.util

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.view.WindowInsetsControllerCompat

fun getStatusBarHeight(): Int {
    val resourceId: Int = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        Resources.getSystem().getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

fun getNavigationBarHeight(): Int {
    val resourceId: Int = Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        Resources.getSystem().getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

fun extendRootViewLayout(window: Window?) {
    if (Build.VERSION.SDK_INT < 25) return

    window?.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}

fun closeRootViewLayout(window: Window?) {
    if (Build.VERSION.SDK_INT < 25) return
    window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}

fun Activity?.setStatusBarColor(
    rootView: View?,
    @ColorInt color: Int,
    isLight: Boolean
) {
    this?.window?.apply {
        statusBarColor = color
        rootView?.also {
            WindowInsetsControllerCompat(this, rootView).apply {
                isAppearanceLightStatusBars = isLight
            }
        }
    }
}

fun setLightStatusBars(isLight: Boolean, activity: Activity?, rootView: View) {
    activity?.window?.let {
        WindowInsetsControllerCompat(it, rootView).isAppearanceLightStatusBars = isLight
    }
}