package com.clonect.feeltalk.mvp_presentation.utils

import android.util.Log

fun infoLog(message: String, tag: String = "FeeltalkInfo") {
    Log.i(tag, message)
}

fun debugLog(message: String, tag: String = "FeeltalkDebug") {
    Log.d(tag, message)
}
