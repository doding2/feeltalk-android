package com.clonect.feeltalk.data.api

import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.domain.model.notification.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {

    @Headers("Authorization: key=${Constants.FCM_SERVER_KEY}", "Content-Type:${Constants.FCM_CONTENT_TYPE}")
    @POST("/fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

}