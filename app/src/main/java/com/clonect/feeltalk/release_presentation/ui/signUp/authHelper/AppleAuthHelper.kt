package com.clonect.feeltalk.release_presentation.ui.signUp.authHelper

import android.content.Context
import androidx.lifecycle.Lifecycle
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AppleAuthHelper {
    companion object {

        suspend fun signIn(context: Context, lifecycle: Lifecycle) = suspendCoroutine { continuation ->
            AppleSignInDialog(context) { state ->
                if (state == null) {
                    continuation.resumeWithException(NullPointerException("애플 연동 실패"))
                    return@AppleSignInDialog
                }
                continuation.resume(state)
            }.show()
        }

        fun logOut() {

        }

    }
}