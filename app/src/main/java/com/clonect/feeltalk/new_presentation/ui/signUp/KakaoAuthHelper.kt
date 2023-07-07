package com.clonect.feeltalk.new_presentation.ui.signUp

import android.content.Context
import com.clonect.feeltalk.common.Quadruple
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KakaoAuthHelper {
    companion object {

        suspend fun signIn(context: Context) = suspendCoroutine { continuation ->
            val browserCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    // 카카오톡 로그인 실패
                    continuation.resumeWithException(error)
                } else if (token != null) {
                    // 카카오톡 로그인 성공
                    UserApiClient.instance.me { user, error ->
                        continuation.resume(
                            Quadruple(
                                token.accessToken,
                                token.refreshToken,
                                user?.kakaoAccount?.email,
                                user?.kakaoAccount?.name
                            )
                        )
                    }
                } else {
                    // 존재할 수 없는 에러
                    continuation.resumeWithException(IllegalStateException("카카오톡 연동에 예상치 못한 에러 발생"))
                }
            }

            UserApiClient.instance.run {
                if (isKakaoTalkLoginAvailable(context)) {
                    // 카카오톡 앱이 설치되어있음
                    loginWithKakaoTalk(context) { token, error ->
                        if (error != null) {
                            // 카카오톡 로그인 실패
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                // 유저가 취소 시킴
                                continuation.resumeWithException(error)
                                return@loginWithKakaoTalk
                            }

                            // 카카오 계정으로 로그인 시도
                            loginWithKakaoAccount(context, callback = browserCallback)
                        } else if (token != null) {
                            // 카카오톡 로그인 성공
                            UserApiClient.instance.me { user, error ->
                                continuation.resume(
                                    Quadruple(
                                        token.accessToken,
                                        token.refreshToken,
                                        user?.kakaoAccount?.email,
                                        user?.kakaoAccount?.name
                                    )
                                )
                            }
                        } else {
                            // 존재할 수 없는 에러
                            continuation.resumeWithException(IllegalStateException("카카오톡 연동에 예상치 못한 에러 발생"))
                        }
                    }
                } else {
                    // 카카오톡 앱이 설치되어있지 않음
                    loginWithKakaoAccount(context, callback = browserCallback)
                }
            }
        }

        suspend fun logOut() = suspendCoroutine { continuation ->
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else {
                    continuation.resume(Unit)
                }
            }
        }

    }
}