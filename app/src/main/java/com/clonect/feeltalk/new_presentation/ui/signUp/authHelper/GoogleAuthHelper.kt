package com.clonect.feeltalk.new_presentation.ui.signUp.authHelper

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.common.Quadruple
import com.clonect.feeltalk.common.Quintuple
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GoogleAuthHelper {
    companion object {

        fun signIn(context: Context, launcher: ActivityResultLauncher<Intent>) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestIdToken(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
                .requestServerAuthCode(BuildConfig.GOOGLE_AUTH_CLIENT_ID, true)
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
            val intent = mGoogleSignInClient.signInIntent

            launcher.launch(intent)
        }

        fun handleSignInData(completedTask: Task<GoogleSignInAccount>): Quintuple<String, String, String?, String?, String?> {
            val account = completedTask.getResult(ApiException::class.java) ?: throw NullPointerException()
            val idToken = account.idToken.toString()
            val serverAuthCode = account.serverAuthCode.toString()
            val email = account.email
            val name = account.displayName
            val uniqueId = account.id
            return Quintuple(idToken,  serverAuthCode, email, name, uniqueId)
        }

        suspend fun logOut(context: Context) = suspendCoroutine { continuation ->
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
            mGoogleSignInClient
                .signOut()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }

    }
}