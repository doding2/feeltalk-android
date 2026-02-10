package com.clonect.feeltalk.release_presentation.ui.signUp.authHelper

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NaverAuthHelper {
    companion object {

        suspend fun signIn(context: Context) = withContext(Dispatchers.Main) {
            val tokens = getToken(context)
            val oauthId = getOauthId()
            return@withContext Triple(tokens.first, tokens.second, oauthId)
        }

        private suspend fun getToken(context: Context) = suspendCoroutine { continuation ->
            val callback = object: OAuthLoginCallback {
                override fun onError(errorCode: Int, message: String) {
                    continuation.resumeWithException(Exception(message))
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    continuation.resumeWithException(Exception(message))
                }

                override fun onSuccess() {
                    val accessToken = NaverIdLoginSDK.getAccessToken()
                    val refreshToken = NaverIdLoginSDK.getRefreshToken()

                    if (accessToken == null || refreshToken == null) {
                        continuation.resumeWithException(NullPointerException("네이버에서 전달된 토큰이 null"))
                        return
                    }

                    continuation.resume(Pair(accessToken, refreshToken))
                }
            }

            NaverIdLoginSDK.authenticate(context, callback)
        }

        private suspend fun getOauthId() = suspendCoroutine { continuation ->
            NidOAuthLogin().callProfileApi(object: NidProfileCallback<NidProfileResponse> {
                override fun onError(errorCode: Int, message: String) {
                    continuation.resumeWithException(Exception(message))
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    continuation.resumeWithException(Exception(message))
                }

                override fun onSuccess(result: NidProfileResponse) {
                    val oauthId = result.profile?.id
                    if (oauthId == null) {
                        continuation.resumeWithException(NullPointerException("네이버에서 전달된 oauthId가 null"))
                        return
                    }
                    continuation.resume(oauthId)
                }
            })
        }

        suspend fun logOut() {
            NaverIdLoginSDK.logout()
        }

    }
}