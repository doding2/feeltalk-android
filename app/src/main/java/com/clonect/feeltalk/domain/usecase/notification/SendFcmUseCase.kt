package com.clonect.feeltalk.domain.usecase.notification

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
                    title = "최종근",
                    message = "야 승준아 너 놀고있지"
                ),
                to = "/topics/Push"
            ),
            onSuccess = {  },
            onFailure = {  }
        )
    }
}