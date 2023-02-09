package com.clonect.feeltalk.data.repository.notification

import com.clonect.feeltalk.data.api.NotificationService
import com.clonect.feeltalk.domain.model.data.notification.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response

class NotificationRepository(
    private val notificationService: NotificationService
) {

    suspend fun postNotification(pushNotification: PushNotification, onSuccess: (Response<ResponseBody>) -> Unit, onFailure: (String) -> Unit) {
        try {
            val response = notificationService.postNotification(pushNotification)
            if (response.isSuccessful) {
                onSuccess(response)
            } else {
                onFailure(response.errorBody().toString())
            }
        } catch (e: Exception) {
            onFailure(e.toString())
        }
    }
}