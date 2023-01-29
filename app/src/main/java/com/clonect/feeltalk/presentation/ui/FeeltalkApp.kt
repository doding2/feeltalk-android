package com.clonect.feeltalk.presentation.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.clonect.feeltalk.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FeeltalkApp: Application() {
    override fun onCreate() {
        super.onCreate()

        initializeKakaoSdk()
        disableNightMode()
    }

    private fun initializeKakaoSdk() {
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    private fun disableNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }



    companion object {
        private var isAppRunning = false
        private var questionIdOfChatFragment: Long? = null

        fun getAppRunning() = isAppRunning

        fun onAppResumed() {
            isAppRunning = true
        }

        fun onAppPaused() {
            isAppRunning = false
        }

        fun getQuestionIdOfShowingChatFragment() = questionIdOfChatFragment

        fun setQuestionIdOfShowingChatFragment(questionId: Long?) {
            questionIdOfChatFragment = questionId
        }
    }
}