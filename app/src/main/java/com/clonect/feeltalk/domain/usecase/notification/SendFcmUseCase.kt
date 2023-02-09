package com.clonect.feeltalk.domain.usecase.notification

import com.clonect.feeltalk.data.repository.notification.NotificationRepository
import com.clonect.feeltalk.domain.model.data.notification.NotificationData
import com.clonect.feeltalk.domain.model.data.notification.PushNotification

class SendFcmUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(fcmToken: String) {
        notificationRepository.postNotification(
            pushNotification = PushNotification(
                data = NotificationData(
                    title = "커플 등록 완료",
                    message = "커플 등록이 완료되었습니다.",
                    type = ""
                ),
                to = "/topics/Push"
            ),
            onSuccess = {  },
            onFailure = {  }
        )
    }
}