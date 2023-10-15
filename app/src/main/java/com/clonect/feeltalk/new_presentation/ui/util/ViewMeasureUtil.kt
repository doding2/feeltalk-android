package com.clonect.feeltalk.new_presentation.ui.util

import android.view.View
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun View.measure(action: View.() -> Number) = suspendCoroutine { continuation ->
    post {
        continuation.resume(action().toFloat())
    }
}