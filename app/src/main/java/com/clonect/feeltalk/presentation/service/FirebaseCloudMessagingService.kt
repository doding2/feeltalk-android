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
import com.clonect.feeltalk.domain.usecase.question.GetQuestionAnswersUseCase
import com.clonect.feeltalk.domain.usecase.question.SaveQuestionToDatabaseUseCase
import com.clonect.feeltalk.domain.usecase.user.CheckUserIsSignedUpUseCase
import com.clonect.feeltalk.presentation.service.notification_observer.AcceptRestoringKeysRequestObserver
import com.clonect.feeltalk.presentation.service.notification_observer.CoupleRegistrationObserver
import com.clonect.feeltalk.presentation.service.notification_observer.FcmNewChatObserver
import com.clonect.feeltalk.presentation.service.notification_observer.QuestionAnswerObserver
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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseCloudMessagingService: FirebaseMessagingService() {

    // App Settings
    @Inject
    lateinit var getAppSettingsUseCase: GetAppSettingsUseCase
    @Inject
    lateinit var saveAppSettingsUseCase: SaveAppSettingsUseCase

    // User
    @Inject
    lateinit var checkUserIsSignedUpUseCase: CheckUserIsSignedUpUseCase

    // User Level Encryptor
    @Inject
    lateinit var userLevelEncryptHelper: UserLevelEncryptHelper

    // Chat
    @Inject
    lateinit var saveChatUseCase: SaveChatUseCase

    // Question
    @Inject
    lateinit var saveQuestionToDatabaseUseCase: SaveQuestionToDatabaseUseCase
    @Inject
    lateinit var getQuestionAnswersUseCase: GetQuestionAnswersUseCase


    companion object {
        const val NOTIFICATION_GROUP_ID = 20000414
        
        const val TODAY_QUESTION_CHANNEL_ID ="feeltalk_today_question_notification"
        const val PARTNER_ANSWERED_CHANNEL_ID ="feeltalk_partner_question_answer_notification"
        const val REQUEST_EMOTION_CHANGE_CHANNEL_ID ="feeltalk_emotion_change_notification"
        const val CHAT_CHANNEL_ID ="feeltalk_chat_notification"
        const val COUPLE_REGISTRATION_CHANNEL_ID ="feeltalk_couple_registration_completed_notification"
        const val ADVERTISING_CHANNEL_ID ="feeltalk_advertising_notification"
        const val REQUEST_KEY_RESTORING_CHANNEL_ID ="feeltalk_request_key_restoring_notification"
        const val ACCEPT_KEY_RESTORING_CHANNEL_ID = "feeltalk_accept_key_restoring_notification"

        const val TYPE_CHAT = "chat"
        const val TYPE_COUPLE_REGISTRATION = "coupleMatch"
        const val TYPE_TODAY_QUESTION = "newQuestion"
        const val TYPE_PARTNER_ANSWERED = "isAnswer"
        const val TYPE_REQUEST_EMOTION_CHANGE = "emotionRequest"
        const val TYPE_REQUEST_KEY_RESTORING = "KeyTrade"
        const val TYPE_ACCEPT_KEY_RESTORING = "KeyTradeOk"
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
        CoroutineScope(Dispatchers.Main).launch {

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

            val isSignedUp = checkUserIsSignedUpUseCase()
            if (isSignedUp is Resource.Error) {
                infoLog("유저가 로그아웃된 상태입니다. fcm을 무시합니다: ${isSignedUp.throwable.localizedMessage}")
                return@launch
            }

            when (data["type"]) {
                TYPE_TODAY_QUESTION -> handleTodayQuestionData(data)
                TYPE_PARTNER_ANSWERED -> handlePartnerAnsweredData(data)
                TYPE_REQUEST_EMOTION_CHANGE -> handleRequestEmotionChangeData(data)
                TYPE_CHAT -> handleChatData(data)
                TYPE_COUPLE_REGISTRATION -> handleCoupleRegistrationData(data)
                TYPE_REQUEST_KEY_RESTORING -> handleRequestKeyRestoringData(data)
                TYPE_ACCEPT_KEY_RESTORING -> handleAcceptRequestKeyRestoringData(data)
                else -> handleOtherCases(data)
            }
        }
    }

    private fun handleTodayQuestionData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val question = data["detail"] ?: return@launch

        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val date = format.format(Date())
        val newQuestion = Question(
            question = question,
            questionDate = date
        ).also {
            saveQuestionToDatabaseUseCase(it)
        }

        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.overall_nav_graph)
            .setDestination(R.id.todayQuestionFragment)
            .setArguments(bundleOf("selectedQuestion" to newQuestion))
            .createPendingIntent()


        val appSettings = getAppSettingsUseCase().apply {
            isNotificationUpdated = true
        }
        saveAppSettingsUseCase(appSettings)


        showNotification(
            title = data["title"] ?: "",
            message = data["message"] ?: "",
            notificationID = TODAY_QUESTION_CHANNEL_ID.toBytesInt(),
            channelID = TODAY_QUESTION_CHANNEL_ID,
            pendingIntent = deepLinkPendingIntent
        )
    }

    private fun handlePartnerAnsweredData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val questionContent = data["detail"] ?: return@launch

        val showingQuestionContent = FeeltalkApp.getQuestionIdOfShowingChatFragment()
        val isAppShowing = FeeltalkApp.getAppRunning()

        if (isAppShowing && showingQuestionContent == questionContent) {
            QuestionAnswerObserver
                .getInstance()
                .setAnswerUpdated(true)
            return@launch
        }

        val answers = (getQuestionAnswersUseCase(questionContent) as? Resource.Success)?.data

        val pendingIntent = if (answers == null) {
            val intent = Intent(this@FirebaseCloudMessagingService, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this@FirebaseCloudMessagingService, 0, intent, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(this@FirebaseCloudMessagingService, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            }
            pendingIntent
        } else if (answers.self == null) {
            val newQuestion = Question(
                question = questionContent
            )
            val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
                .setGraph(R.navigation.overall_nav_graph)
                .setDestination(R.id.todayQuestionFragment)
                .setArguments(bundleOf("selectedQuestion" to newQuestion))
                .createPendingIntent()
            deepLinkPendingIntent
        } else {
            val newQuestion = Question(
                question = questionContent
            )
            val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
                .setGraph(R.navigation.overall_nav_graph)
                .setDestination(R.id.chatFragment)
                .setArguments(bundleOf("selectedQuestion" to newQuestion))
                .createPendingIntent()
            deepLinkPendingIntent
        }

        val appSettings = getAppSettingsUseCase().apply {
            isNotificationUpdated = true
        }
        saveAppSettingsUseCase(appSettings)

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

        val question = Question(
            question = questionContent
        )

        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.overall_nav_graph)
            .setDestination(R.id.chatFragment)
            .setArguments(
                bundleOf("selectedQuestion" to question)
            )
            .createPendingIntent()

        showNotification(
            title = data["title"] ?: "",
            message = data["message"] ?: "",
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

        CoroutineScope(Dispatchers.IO).launch {
            val appSettings = getAppSettingsUseCase().apply {
                isNotificationUpdated = true
            }
            saveAppSettingsUseCase(appSettings)
        }

        val title = data["title"] ?: ""
        val message = data["message"] ?: ""

        val isAppRunning = FeeltalkApp.getAppRunning()
        if (!isAppRunning) {
            showNotification(
                title = title,
                message = message,
                notificationID = COUPLE_REGISTRATION_CHANNEL_ID.toBytesInt(),
                channelID = COUPLE_REGISTRATION_CHANNEL_ID,
                pendingIntent = pendingIntent
            )
        }
    }

    private fun handleRequestKeyRestoringData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.overall_nav_graph)
            .setDestination(R.id.keyRestoringAcceptFragment)
            .createPendingIntent()

        val title = data["title"] ?: ""
        val message = data["message"] ?: ""

        showNotification(
            title = title,
            message = message,
            notificationID = REQUEST_KEY_RESTORING_CHANNEL_ID.toBytesInt(),
            channelID = REQUEST_KEY_RESTORING_CHANNEL_ID,
            pendingIntent = pendingIntent
        )
    }

    private fun handleAcceptRequestKeyRestoringData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        AcceptRestoringKeysRequestObserver
            .getInstance()
            .setPartnerAccepted(true)

        val isAppRunning = FeeltalkApp.getAppRunning()
        if (!isAppRunning) {
            showNotification(
                title = data["title"] ?: "",
                message = data["message"] ?: "",
                notificationID = ACCEPT_KEY_RESTORING_CHANNEL_ID.toBytesInt(),
                channelID = ACCEPT_KEY_RESTORING_CHANNEL_ID,
                pendingIntent = null
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
        notificationID: Int = System.currentTimeMillis().toInt(),
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
            .setGroup(notificationID.toString())
            .setGroupSummary(true)
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
        REQUEST_KEY_RESTORING_CHANNEL_ID -> "키 복구 요청"
        ACCEPT_KEY_RESTORING_CHANNEL_ID -> "키 복구 요청 수락"
        else -> ""
    }

    private fun getChannelDescription(channelID: String): String = when (channelID) {
        TODAY_QUESTION_CHANNEL_ID -> "오늘의 질문"
        PARTNER_ANSWERED_CHANNEL_ID -> "상대방의 질문 답변"
        REQUEST_EMOTION_CHANGE_CHANNEL_ID -> "연인 감정 물어보기"
        CHAT_CHANNEL_ID -> "채팅"
        ADVERTISING_CHANNEL_ID -> "광고"
        COUPLE_REGISTRATION_CHANNEL_ID -> "커플 등록"
        REQUEST_KEY_RESTORING_CHANNEL_ID -> "키 복구 요청"
        ACCEPT_KEY_RESTORING_CHANNEL_ID -> "키 복구 요청 수락"
        else -> ""
    }
}