package com.clonect.feeltalk.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.usecase.question.GetQuestionByIdUseCase
import com.clonect.feeltalk.domain.usecase.chat.SaveChatUseCase
import com.clonect.feeltalk.domain.usecase.notification.SaveFcmTokenUseCase
import com.clonect.feeltalk.presentation.service.notification_observer.CoupleRegistrationObserver
import com.clonect.feeltalk.presentation.service.notification_observer.FcmNewChatObserver
import com.clonect.feeltalk.presentation.ui.FeeltalkApp
import com.clonect.feeltalk.presentation.ui.activity.MainActivity
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextUInt

@AndroidEntryPoint
class FirebaseCloudMessagingService: FirebaseMessagingService() {

    // Fcm
    @Inject
    lateinit var saveFcmTokenUseCase: SaveFcmTokenUseCase

    // Chat
    @Inject
    lateinit var saveChatUseCase: SaveChatUseCase
    @Inject
    lateinit var getQuestionByIdUseCase: GetQuestionByIdUseCase


    companion object {
        const val TODAY_QUESTION_CHANNEL_ID ="feeltalk_today_question_notification"
        const val PARTNER_QUESTION_ANSWER_CHANNEL_ID ="feeltalk_partner_question_answer_notification"
        const val EMOTION_CHANGE_CHANNEL_ID ="feeltalk_emotion_change_notification"
        const val CHAT_CHANNEL_ID ="feeltalk_chat_notification"
        const val ADVERTISING_CHANNEL_ID ="feeltalk_advertising_notification"
        const val COUPLE_REGISTRATION_COMPLETED_CHANNEL_ID ="feeltalk_couple_registration_completed_notification"
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        saveFcmTokenUseCase(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val isAppRunning = FeeltalkApp.getAppRunning()
        infoLog("isAppRunning: $isAppRunning")
        infoLog("message: ${message.data}")

        val data = if (message.data["data"] == null) {
            message.data
        } else {
            Gson().fromJson(message.data["data"], Map::class.java) as? Map<String, String>
                ?: message.data
        }

        infoLog("parsed message: $data")

        when (data["type"]) {
            "today_question" -> handleTodayQuestionData(data)
            "chat" -> handleChatData(data)
            "커플매칭성공" -> handleCoupleRegistrationData(data)
            else -> handleOtherCases(data)
        }
    }

    private fun handleCoupleRegistrationData(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val isAppRunning = FeeltalkApp.getAppRunning()
        if (isAppRunning) {
            CoupleRegistrationObserver
                .getInstance()
                .setCoupleRegistrationCompleted(true)
            return
        }

        val title = data["title"] ?: "제목 읽기 실패"
        val message = data["message"] ?: "내용 읽기 실패"

        showNotification(
            title = title,
            message = message,
            channelID = COUPLE_REGISTRATION_COMPLETED_CHANNEL_ID,
            pendingIntent = pendingIntent
        )
    }

    private fun handleOtherCases(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val title = data["title"] ?: "제목 읽기 실패"
        val message = data["message"] ?: "내용 읽기 실패"

        showNotification(
            title = title,
            message = message,
            channelID = TODAY_QUESTION_CHANNEL_ID,
            pendingIntent = pendingIntent
        )
    }

    private fun handleTodayQuestionData(data: Map<String, String>) {
        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.overall_nav_graph)
            .setDestination(R.id.todayQuestionFragment)
            .setArguments(
                bundleOf("selectedQuestion" to Question(
                    id = Random.nextUInt().toLong(),
                    contentPrefix = data["contentPrefix"] ?: "",
                    content = data["content"] ?: "",
                    contentSuffix = data["contentSuffix"] ?: "",
                    partnerAnswer = data["partnerAnswer"] ?: "",
                    partnerAnswerDate = data["partnerAnswerDate"] ?: "",
                ))
            )
            .createPendingIntent()

        showNotification(
            title = data["title"] ?: "오늘의 질문 도착",
            message = data["message"] ?: "새로운 질문이 도착했어요",
            channelID = TODAY_QUESTION_CHANNEL_ID,
            pendingIntent = deepLinkPendingIntent
        )
    }

    private fun handleChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val questionId = data["questionId"]?.toLong() ?: 0L

        val chat = Chat(
            questionId = questionId,
            ownerEmail = "partner@email.com",
            content = data["content"].toString(),
            date = data["date"].toString())

        val saveResult = saveChatUseCase(chat)
        if (saveResult !is Resource.Success)
            return@launch

        val showingQuestionId = FeeltalkApp.getQuestionIdOfShowingChatFragment()
        if (showingQuestionId == questionId) {
            FcmNewChatObserver
                .getInstance()
                .setNewChat(chat)
        }

        val questionResult = getQuestionByIdUseCase(questionId)
        if (questionResult !is Resource.Success)
            return@launch
        val question = questionResult.data

        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.overall_nav_graph)
            .setDestination(R.id.chatFragment)
            .setArguments(
                bundleOf("selectedQuestion" to Question(
                    id = question.id,
                    contentPrefix = data["contentPrefix"] ?: question.contentPrefix,
                    content = data["content"] ?: question.content,
                    contentSuffix = data["contentSuffix"] ?: question.contentSuffix,
                    partnerAnswer = data["partnerAnswer"] ?: question.partnerAnswer,
                    partnerAnswerDate = data["partnerAnswerDate"] ?: question.partnerAnswerDate,
                    myAnswer = data["myAnswer"] ?: question.myAnswer,
                    myAnswerDate = data["myAnswerDate"] ?: question.myAnswerDate,
                ))
            )
            .createPendingIntent()

        withContext(Dispatchers.Main) {
            showNotification(
                title = data["title"].toString(),
                message = data["title"].toString(),
                channelID = CHAT_CHANNEL_ID,
                pendingIntent = pendingIntent
            )
        }
    }



    private fun showNotification(title: String, message: String, channelID: String, pendingIntent: PendingIntent?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        createNotificationChannel(
            notificationManager = notificationManager,
            channelID = channelID
        )

        val notification = NotificationCompat.Builder(this, TODAY_QUESTION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    private fun createNotificationChannel(
        notificationManager: NotificationManager,
        channelID: String,
        channelName: String = getChannelName(channelID),
        channelDescription: String = getChannelDescription(channelID)
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getChannelName(channelID: String): String = when (channelID) {
        TODAY_QUESTION_CHANNEL_ID -> "오늘의 질문"
        PARTNER_QUESTION_ANSWER_CHANNEL_ID -> "상대방의 질문 답변"
        EMOTION_CHANGE_CHANNEL_ID -> "연인의 감정"
        CHAT_CHANNEL_ID -> "채팅"
        ADVERTISING_CHANNEL_ID -> "광고"
        COUPLE_REGISTRATION_COMPLETED_CHANNEL_ID -> "커플 등록 완료"
        else -> ""
    }

    private fun getChannelDescription(channelID: String): String = when (channelID) {
        TODAY_QUESTION_CHANNEL_ID -> "오늘의 질문"
        PARTNER_QUESTION_ANSWER_CHANNEL_ID -> "상대방의 질문 답변"
        EMOTION_CHANGE_CHANNEL_ID -> "연인의 감정"
        CHAT_CHANNEL_ID -> "채팅"
        ADVERTISING_CHANNEL_ID -> "광고"
        COUPLE_REGISTRATION_COMPLETED_CHANNEL_ID -> "커플 등록 완료"
        else -> ""
    }
}