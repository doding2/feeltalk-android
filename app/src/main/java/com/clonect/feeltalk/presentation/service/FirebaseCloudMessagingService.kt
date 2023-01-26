package com.clonect.feeltalk.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.clonect.feeltalk.R
import com.clonect.feeltalk.domain.model.question.Question
import com.clonect.feeltalk.domain.usecase.SaveFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseCloudMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var saveFcmTokenUseCase: SaveFcmTokenUseCase

    companion object {
        const val CHANNEL_ID ="feeltalk_default_notification_id"
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        saveFcmTokenUseCase(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.overall_nav_graph)
            .setDestination(R.id.todayQuestionFragment)
            .setArguments(
                bundleOf("selectedQuestion" to Question(
                    id = 1234,
                    contentPrefix = "알림으로부터 온 ",
                    content = "질문입니다",
                    contentSuffix = " !",
                    myAnswer = "",
                    partnerAnswer = "파트너의 대답",
                    myAnswerDate = "",
                    partnerAnswerDate = "2010.06.11 오전 12:30",
                ))
            )
            .createPendingIntent()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
            .setContentIntent(deepLinkPendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "연인의 알림"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "띠링 띠링"
        }
        notificationManager.createNotificationChannel(channel)
    }
}