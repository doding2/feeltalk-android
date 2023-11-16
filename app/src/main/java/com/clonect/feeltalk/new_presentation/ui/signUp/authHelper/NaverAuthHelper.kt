package com.clonect.feeltalk.new_presentation.ui.signUp.authHelper

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NaverAuthHelper {
    companion object {

        suspend fun signIn(context: Context) = suspendCoroutine { continuation ->
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

            NidOAuthLogin().callProfileApi(object: NidProfileCallback<NidProfileResponse> {
                override fun onError(errorCode: Int, message: String) {
                }

                override fun onFailure(httpStatus: Int, message: String) {
                }

                override fun onSuccess(result: NidProfileResponse) {
                    result.profile?.id
                }
            })
        }

        suspend fun logOut() {
            NaverIdLoginSDK.logout()
        }

    }
}