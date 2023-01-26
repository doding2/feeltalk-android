package com.clonect.feeltalk.domain.usecase

import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.data.api.NotificationService
import com.clonect.feeltalk.data.repository.notification.NotificationRepository
import com.clonect.feeltalk.domain.model.notification.NotificationData
import com.clonect.feeltalk.domain.model.notification.PushNotification

class SendFcmUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(fcmToken: String) {
        notificationRepository.postNotification(
            pushNotification = PushNotification(
                data = NotificationData(
                    title = "알림 제목",
                    message = "알림 내용"
                ),
                to = fcmToken
            ),
            onSuccess = {  },
            onFailure = {  }
        )
    }
}