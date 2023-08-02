package com.clonect.feeltalk.new_presentation.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.R
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FeeltalkApp: Application() {

    override fun onCreate() {
        super.onCreate()

        disableNightMode()
        initNaver()
        initKakao()
        initFcm()
    }

    private fun initFcm() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {  }
    }

    private fun initKakao() {
        KakaoSdk.init(context = this, appKey = BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    private fun initNaver() {
        try {
            NaverIdLoginSDK.initialize(
                context = applicationContext,
                clientId = BuildConfig.NAVER_AUTH_CLIENT_ID,
                clientSecret = BuildConfig.NAVER_AUTH_CLIENT_SECRET,
                clientName = getString(R.string.app_name),
            )
        } catch (e: Exception) {
            infoLog("Naver sdk initialize error: ${e.localizedMessage}")
        }
    }

    private fun disableNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }



    companion object {
        private var isAppScreenActive = false
        private var isUserInChat = false

        fun getAppScreenActive() = isAppScreenActive

        fun onAppScreenResumed() {
            isAppScreenActive = true
        }

        fun onAppScreenPaused() {
            isAppScreenActive = false
        }

        fun onAppDestroyed() {
            isAppScreenActive = false
        }


        fun getUserInChat() = isUserInChat

        fun setUserInChat(isInChat: Boolean) {
            isUserInChat = isInChat
        }

    }
}