package com.clonect.feeltalk.presentation.ui.bottom_navigation

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.clonect.feeltalk.domain.model.data.notification.Topics
import com.clonect.feeltalk.domain.usecase.notification.GetFcmTokenUseCase
import com.clonect.feeltalk.domain.usecase.notification.SaveFcmTokenUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BottomNavigationViewModel @Inject constructor(
    @Named("AppSettings")
    private val pref: SharedPreferences,
): ViewModel() {

    init {
        initFirebase()
    }

    private fun initFirebase() {
        val isPushNotificationEnabled = pref.getBoolean("isPushNotificationEnabled", false)
        val isUsageInfoNotificationEnabled = pref.getBoolean("isUsageInfoNotificationEnabled", false)
        enablePushNotificationEnabled(isPushNotificationEnabled)
        enableUsageInfoNotification(isUsageInfoNotificationEnabled)
    }

    fun enablePushNotificationEnabled(enabled: Boolean) {
        FirebaseMessaging.getInstance().apply {
            token.addOnSuccessListener {
                infoLog("fcmToken: $it")
                if (enabled) {
                    subscribeToTopic(Topics.Push.text)
                }
                else {
                    unsubscribeFromTopic(Topics.Push.text)
                }
                pref.edit()
                    .putBoolean("isPushNotificationEnabled", enabled)
                    .apply()
            }
        }
    }


    fun enableUsageInfoNotification(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        FirebaseMessaging.getInstance().apply {
            token.addOnSuccessListener {
                if (enabled) {
                    subscribeToTopic(Topics.UsageInfo.text)
                } else {
                    unsubscribeFromTopic(Topics.UsageInfo.text)
                }
                pref.edit()
                    .putBoolean("isUsageInfoNotificationEnabled", enabled)
                    .apply()
            }
        }
    }

}