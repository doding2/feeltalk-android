package com.clonect.feeltalk.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.usecase.app_settings.GetAppSettingsUseCase
import com.clonect.feeltalk.domain.usecase.app_settings.SaveAppSettingsUseCase
import com.clonect.feeltalk.domain.usecase.chat.SaveChatUseCase
import com.clonect.feeltalk.domain.usecase.question.GetQuestionByContentFromDataBaseUseCase
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
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseCloudMessagingService: FirebaseMessagingService() {

    // App Settings
    @Inject
    lateinit var getAppSettingsUseCase: GetAppSettingsUseCase
    @Inject
    lateinit var saveAppSettingsUseCase: SaveAppSettingsUseCase

    // User Level Encryptor
    @Inject
    lateinit var userLevelEncryptHelper: UserLevelEncryptHelper

    // Chat
    @Inject
    lateinit var saveChatUseCase: SaveChatUseCase

    // Question
    @Inject
    lateinit var getQuestionByContentFromDataBaseUseCase: GetQuestionByContentFromDataBaseUseCase


    companion object {
        const val TODAY_QUESTION_CHANNEL_ID ="feeltalk_today_question_notification"
        const val PARTNER_ANSWERED_CHANNEL_ID ="feeltalk_partner_question_answer_notification"
        const val REQUEST_EMOTION_CHANGE_CHANNEL_ID ="feeltalk_emotion_change_notification"
        const val CHAT_CHANNEL_ID ="feeltalk_chat_notification"
        const val COUPLE_REGISTRATION_CHANNEL_ID ="feeltalk_couple_registration_completed_notification"
        const val ADVERTISING_CHANNEL_ID ="feeltalk_advertising_notification"

        const val TYPE_CHAT = "chat"
        const val TYPE_COUPLE_REGISTRATION = "커플매칭성공"
        const val TYPE_TODAY_QUESTION = "newQuestion"
        const val TYPE_PARTNER_ANSWERED = "isAnswer"
        const val TYPE_REQUEST_EMOTION_CHANGE = "emotionRequest"

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        val appSettings = getAppSettingsUseCase()
        appSettings.fcmToken = newToken
        CoroutineScope(Dispatchers.IO).launch {
            saveAppSettingsUseCase(appSettings)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val isAppRunning = FeeltalkApp.getAppRunning()
        infoLog("isAppRunning: $isAppRunning")

        val data = if (message.data["data"] == null) {
            message.data
        } else {
            try {
                Gson().fromJson(message.data["data"], Map::class.java) as? Map<String, String> ?: message.data
            } catch (_: Exception) {
                message.data
            }
        }
        infoLog("새로 도착한 FCM: ${message.data}")
        infoLog("파싱된 FCM: $data")

        when (data["type"]) {
            TYPE_TODAY_QUESTION -> handleTodayQuestionData(data)
            TYPE_PARTNER_ANSWERED -> handlePartnerAnsweredData(data)
            TYPE_REQUEST_EMOTION_CHANGE -> handleRequestEmotionChangeData(data)
            TYPE_CHAT -> handleChatData(data)
            TYPE_COUPLE_REGISTRATION -> handleCoupleRegistrationData(data)
            else -> handleOtherCases(data)
        }
    }

    private fun handleTodayQuestionData(data: Map<String, String>) {
        val question = data["detail"] ?: return
        val newQuestion = Question(
            question = question
        )

        // TODO 질문 리스트 내부 저장소에도 질문 추가

        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.overall_nav_graph)
            .setDestination(R.id.todayQuestionFragment)
            .setArguments(bundleOf("selectedQuestion" to newQuestion))
            .createPendingIntent()

        showNotification(
            title = data["title"] ?: "",
            message = data["message"] ?: "",
            notificationID = TODAY_QUESTION_CHANNEL_ID.toBytesInt(),
            channelID = TODAY_QUESTION_CHANNEL_ID,
            pendingIntent = deepLinkPendingIntent
        )
    }

    private fun handlePartnerAnsweredData(data: Map<String, String>) {

        // TODO DB에서 질문 가져와서 내가 대답 했는지 확인 후
        // 했으면 채팅으로
        // 안 했으면 질문으로 보내기

        val questionContent = data["detail"] ?: return
//        val newQuestion = Question(
//            question = questionContent
//        )
//
//        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
//            .setGraph(R.navigation.overall_nav_graph)
//            .setDestination(R.id.todayQuestionFragment)
//            .setArguments(bundleOf("selectedQuestion" to newQuestion))
//            .createPendingIntent()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        showNotification(
            title = data["title"] ?: "",
            message = data["message"] ?: "",
            notificationID = questionContent.toBytesInt(),
            channelID = PARTNER_ANSWERED_CHANNEL_ID,
            pendingIntent = pendingIntent
        )
    }

    private fun handleRequestEmotionChangeData(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        showNotification(
            title = data["title"] ?: "",
            message = data["message"] ?: "",
            notificationID = REQUEST_EMOTION_CHANGE_CHANNEL_ID.toBytesInt(),
            channelID = REQUEST_EMOTION_CHANGE_CHANNEL_ID,
            pendingIntent = pendingIntent
        )
    }

    private fun handleChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val questionContent = data["title_detail"] ?: return@launch
        val chatMessage = data["message_detail"]?.let { userLevelEncryptHelper.decryptPartnerText(it) } ?: "(Server Error)"
        val date = (data["createAt"] ?: "").replace("T", " ")

        val chat = Chat(
            question = questionContent,
            owner = "partner",
            message = chatMessage,
            date = date
        )

        val showingQuestionContent = FeeltalkApp.getQuestionIdOfShowingChatFragment()
        val isAppShowing = FeeltalkApp.getAppRunning()

        val saveResult = saveChatUseCase(chat)
        if (saveResult !is Resource.Success) {
            // 채팅 저장이 실패하고
            // 보이는 화면이 이 채팅의 채팅방일때
            if (showingQuestionContent == questionContent) {
                FcmNewChatObserver
                    .getInstance()
                    .setNewChat(chat)
                return@launch
            }
        }


        if (showingQuestionContent == questionContent && isAppShowing) {
            return@launch
        }

        val questionResult = getQuestionByContentFromDataBaseUseCase(questionContent)
        val question = if (questionResult is Resource.Success) {
            questionResult.data
        } else {
            Question(question = questionContent)
        }

        // TODO 위에서 수정된 질문 DB에 저장하기

        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.overall_nav_graph)
            .setDestination(R.id.chatFragment)
            .setArguments(
                bundleOf("selectedQuestion" to question)
            )
            .createPendingIntent()

        showNotification(
            title = data["title"].toString(),
            message = data["message"].toString(),
            notificationID = questionContent.toBytesInt(),
            channelID = CHAT_CHANNEL_ID,
            pendingIntent = pendingIntent
        )
    }

    private fun handleCoupleRegistrationData(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        
        CoupleRegistrationObserver
            .getInstance()
            .setCoupleRegistrationCompleted(true)

        val title = data["title"] ?: ""
        val message = data["message"] ?: ""

        val isAppRunning = FeeltalkApp.getAppRunning()
        if (!isAppRunning) {
            showNotification(
                title = "커플 등록 요청",
                message = "상대방으로부터 커플 등록 요청이 왔습니다.",
                notificationID = COUPLE_REGISTRATION_CHANNEL_ID.toBytesInt(),
                channelID = COUPLE_REGISTRATION_CHANNEL_ID,
                pendingIntent = pendingIntent
            )
        }
    }

    private fun handleOtherCases(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val title = data["title"] ?: ""
        val message = data["message"] ?: ""

        showNotification(
            title = title,
            message = message,
            notificationID = title.toBytesInt(),
            channelID = ADVERTISING_CHANNEL_ID,
            pendingIntent = pendingIntent
        )
    }


    private fun String.toBytesInt(): Int {
        val bytes = encodeToByteArray()
        var result = 0
        for (i in bytes.indices) {
            result = result or (bytes[i].toInt() shl 8 * i)
        }
        return result
    }



    private fun showNotification(
        title: String,
        message: String,
        notificationID: Int = Random.nextInt(),
        channelID: String,
        pendingIntent: PendingIntent?,
    ) {
        val appSettings = getAppSettingsUseCase()
        if (!appSettings.isPushNotificationEnabled) return

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
            notificationManager = notificationManager,
            channelID = channelID
        )

        val notification = NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setColor(ContextCompat.getColor(applicationContext, R.color.white))
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
        PARTNER_ANSWERED_CHANNEL_ID -> "상대방의 질문 답변"
        REQUEST_EMOTION_CHANGE_CHANNEL_ID -> "연인 감정 물어보기"
        CHAT_CHANNEL_ID -> "채팅"
        ADVERTISING_CHANNEL_ID -> "광고"
        COUPLE_REGISTRATION_CHANNEL_ID -> "커플 등록"
        else -> ""
    }

    private fun getChannelDescription(channelID: String): String = when (channelID) {
        TODAY_QUESTION_CHANNEL_ID -> "오늘의 질문"
        PARTNER_ANSWERED_CHANNEL_ID -> "상대방의 질문 답변"
        REQUEST_EMOTION_CHANGE_CHANNEL_ID -> "연인 감정 물어보기"
        CHAT_CHANNEL_ID -> "채팅"
        ADVERTISING_CHANNEL_ID -> "광고"
        COUPLE_REGISTRATION_CHANNEL_ID -> "커플 등록"
        else -> ""
    }
}