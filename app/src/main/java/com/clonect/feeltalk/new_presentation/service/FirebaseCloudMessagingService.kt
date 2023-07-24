package com.clonect.feeltalk.new_presentation.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.domain.usecase.user.GetUserIsActiveUseCase
import com.clonect.feeltalk.domain.usecase.user.SetUserIsActiveUseCase
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.model.chat.VoiceChat
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper.Companion.CREATE_COUPLE_CHANNEL_ID
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper.Companion.TYPE_CHAT_ROOM_STATE
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper.Companion.TYPE_CREATE_COUPLE
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper.Companion.TYPE_TEXT_CHATTING
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper.Companion.TYPE_VOICE_CHATTING
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.CreateCoupleObserver
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.NewChatObserver
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.PartnerChatRoomStateObserver
import com.clonect.feeltalk.new_presentation.ui.FeeltalkApp
import com.clonect.feeltalk.new_presentation.ui.activity.MainActivity
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class FirebaseCloudMessagingService: FirebaseMessagingService() {

    // Notification
    @Inject
    lateinit var notificationHelper: NotificationHelper

    // App Settings
    @Inject
    lateinit var getAppSettingsUseCase: GetAppSettingsUseCase
    @Inject
    lateinit var saveAppSettingsUseCase: SaveAppSettingsUseCase

    // Mixpanel
    @Inject
    lateinit var getMixpanelAPIUseCase: GetMixpanelAPIUseCase
    @Inject
    lateinit var getUserIsActiveUseCase: GetUserIsActiveUseCase
    @Inject
    lateinit var setUserIsActiveUseCase: SetUserIsActiveUseCase



    companion object {

        suspend fun getFcmToken() = suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().apply {
                token
                    .addOnSuccessListener {
                        continuation.resume(it)
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            }
        }
        
//        const val TODAY_QUESTION_CHANNEL_ID ="feeltalk_today_question_notification"
//        const val PARTNER_ANSWERED_CHANNEL_ID ="feeltalk_partner_question_answer_notification"
//        const val REQUEST_EMOTION_CHANGE_CHANNEL_ID ="feeltalk_emotion_change_notification"
//        const val CHAT_CHANNEL_ID ="feeltalk_chat_notification"
//        const val COUPLE_REGISTRATION_CHANNEL_ID ="feeltalk_couple_registration_completed_notification"
//        const val ADVERTISING_CHANNEL_ID ="feeltalk_advertising_notification"
//        const val REQUEST_KEY_RESTORING_CHANNEL_ID ="feeltalk_request_key_restoring_notification"
//        const val ACCEPT_KEY_RESTORING_CHANNEL_ID = "feeltalk_accept_key_restoring_notification"

//        const val TYPE_CHAT = "chat"
//        const val TYPE_COUPLE_REGISTRATION = "coupleMatch"
//        const val TYPE_TODAY_QUESTION = "newQuestion"
//        const val TYPE_PARTNER_ANSWERED = "isAnswer"
//        const val TYPE_REQUEST_EMOTION_CHANGE = "emotionRequest"
//        const val TYPE_REQUEST_KEY_RESTORING = "KeyTrade"
//        const val TYPE_ACCEPT_KEY_RESTORING = "KeyTradeOk"
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

            val isAppRunning = FeeltalkApp.getAppScreenRunning()
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
            infoLog("새로운 FCM 메시지:\n$data")

//            val isSignedUp = checkUserIsSignedUpUseCase()
//            if (isSignedUp is Resource.Error) {
//                infoLog("유저가 로그아웃된 상태입니다. fcm을 무시합니다: ${isSignedUp.throwable.localizedMessage}")
//                return@launch
//            }

            when (data["type"]) {
                TYPE_CREATE_COUPLE -> handleCreateCoupleData(data)
                TYPE_CHAT_ROOM_STATE -> handleChatRoomStateData(data)
                TYPE_TEXT_CHATTING -> handleTextChatData(data)
                TYPE_VOICE_CHATTING -> handleVoiceChatData(data)

//                TYPE_TODAY_QUESTION -> handleTodayQuestionData(data)
//                TYPE_PARTNER_ANSWERED -> handlePartnerAnsweredData(data)
//                TYPE_REQUEST_EMOTION_CHANGE -> handleRequestEmotionChangeData(data)
//                TYPE_CHAT -> handleChatData(data)
//                TYPE_COUPLE_REGISTRATION -> handleCoupleRegistrationData(data)
//                TYPE_REQUEST_KEY_RESTORING -> handleRequestKeyRestoringData(data)
//                TYPE_ACCEPT_KEY_RESTORING -> handleAcceptRequestKeyRestoringData(data)
//                else -> handleOtherCases(data)
            }
        }
    }


    private fun handleCreateCoupleData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        CreateCoupleObserver.getInstance()
            .setCoupleCreated(true)

        if (FeeltalkApp.getAppScreenRunning()) {
            return@launch
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        notificationHelper.showNormalNotification(
            title = data["title"] ?: "연인 등록 완료",
            message = data["message"] ?: "연인 등록에 성공했습니다",
            channelID = CREATE_COUPLE_CHANNEL_ID,
            pendingIntent = pendingIntent
        )
    }

    private fun handleChatRoomStateData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val isInChat = data["isInChat"]?.toBoolean() ?: return@launch
        PartnerChatRoomStateObserver.getInstance()
            .setInChat(isInChat)
    }

    private fun handleTextChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val message = data["message"] ?: return@launch
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch

        NewChatObserver.getInstance()
            .setNewChat(
                TextChat(
                    index = index,
                    pageNo = pageIndex,
                    chatSender = "partner",
                    isRead = isRead,
                    createAt = createAt,
                    message = message
                )
            )

        infoLog("isAppScreenRunning: ${FeeltalkApp.getAppScreenRunning()}, isUserInChat: ${FeeltalkApp.getUserInChat()}")
        if (FeeltalkApp.getAppScreenRunning() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            title = "연인",
            message = message
        )
    }

    private fun handleVoiceChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val url = data["url"] ?: return@launch
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch

        NewChatObserver.getInstance()
            .setNewChat(
                VoiceChat(
                    index = index,
                    pageNo = pageIndex,
                    chatSender = "partner",
                    isRead = isRead,
                    createAt = createAt,
                    url = url
                )
            )

        infoLog("isAppScreenRunning: ${FeeltalkApp.getAppScreenRunning()}, isUserInChat: ${FeeltalkApp.getUserInChat()}")
        if (FeeltalkApp.getAppScreenRunning() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            title = "연인",
            message = "(보이스 채팅)"
        )
    }















//    private fun handleTodayQuestionData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
//        val question = data["detail"] ?: return@launch
//
//        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
//        val date = format.format(Date())
//        val newQuestion = Question(
//            question = question,
//            questionDate = date
//        ).also {
//            saveQuestionToDatabaseUseCase(it)
//        }
//
//        val isActive = getUserIsActiveUseCase()
//        if (!isActive) {
//            val result = getUserInfoUseCase()
//            if (result is Resource.Success) {
//                val userInfo = result.data
//                val mixpanel = getMixpanelAPIUseCase()
//                mixpanel.identify(userInfo.email, true)
//                mixpanel.registerSuperProperties(JSONObject().apply {
//                    put("gender", userInfo.gender)
//                })
//
//                mixpanel.track("Deactivate User")
//                mixpanel.people.set("isActive", false)
//
//                setUserIsActiveUseCase()
//            }
//        }
//
//
//        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
////            .setGraph(R.navigation.overall_nav_graph)
//            .setDestination(R.id.todayQuestionFragment)
//            .setArguments(bundleOf("selectedQuestion" to newQuestion))
//            .createPendingIntent()
//
//
//        val appSettings = getAppSettingsUseCase().apply {
//            isNotificationUpdated = true
//        }
//        saveAppSettingsUseCase(appSettings)
//
//
//        showNotification(
//            title = data["title"] ?: "",
//            message = data["message"] ?: "",
//            notificationID = TODAY_QUESTION_CHANNEL_ID.toBytesInt(),
//            channelID = TODAY_QUESTION_CHANNEL_ID,
//            pendingIntent = deepLinkPendingIntent
//        )
//    }
//
//    private fun handlePartnerAnsweredData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
//        val questionContent = data["detail"] ?: return@launch
//
//        val showingQuestionContent = FeeltalkApp.getQuestionIdOfShowingChatFragment()
//        val isAppShowing = FeeltalkApp.getAppRunning()
//
//        if (isAppShowing && showingQuestionContent == questionContent) {
//            QuestionAnswerObserver
//                .getInstance()
//                .setAnswerUpdated(true)
//            return@launch
//        }
//
//        val answers = (getQuestionAnswersUseCase(questionContent) as? Resource.Success)?.data
//
//        val pendingIntent = if (answers == null) {
//            val intent = Intent(this@FirebaseCloudMessagingService, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//            val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                PendingIntent.getActivity(this@FirebaseCloudMessagingService, 0, intent, PendingIntent.FLAG_MUTABLE)
//            } else {
//                PendingIntent.getActivity(this@FirebaseCloudMessagingService, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//            }
//            pendingIntent
//        } else if (answers.self == null) {
//            val newQuestion = Question(
//                question = questionContent
//            )
//            val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
////                .setGraph(R.navigation.overall_nav_graph)
//                .setDestination(R.id.todayQuestionFragment)
//                .setArguments(bundleOf("selectedQuestion" to newQuestion))
//                .createPendingIntent()
//            deepLinkPendingIntent
//        } else {
//            val newQuestion = Question(
//                question = questionContent
//            )
//            val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
////                .setGraph(R.navigation.overall_nav_graph)
//                .setDestination(R.id.chatFragment)
//                .setArguments(bundleOf("selectedQuestion" to newQuestion))
//                .createPendingIntent()
//            deepLinkPendingIntent
//        }
//
//        val appSettings = getAppSettingsUseCase().apply {
//            isNotificationUpdated = true
//        }
//        saveAppSettingsUseCase(appSettings)
//
//        showNotification(
//            title = data["title"] ?: "",
//            message = data["message"] ?: "",
//            notificationID = questionContent.toBytesInt(),
//            channelID = PARTNER_ANSWERED_CHANNEL_ID,
//            pendingIntent = pendingIntent
//        )
//    }
//
//    private fun handleRequestEmotionChangeData(data: Map<String, String>) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//        } else {
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        }
//
//        showNotification(
//            title = data["title"] ?: "",
//            message = data["message"] ?: "",
//            notificationID = REQUEST_EMOTION_CHANGE_CHANNEL_ID.toBytesInt(),
//            channelID = REQUEST_EMOTION_CHANGE_CHANNEL_ID,
//            pendingIntent = pendingIntent
//        )
//    }
//
//    private fun handleChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
//        val questionContent = data["title_detail"] ?: return@launch
//        val chatMessage = data["message_detail"]?.let { userLevelEncryptHelper.decryptPartnerText(it) } ?: "(Server Error)"
//        val date = (data["createAt"] ?: "").replace("T", " ")
//
//        val chat2 = Chat2(
//            question = questionContent,
//            owner = "partner",
//            message = chatMessage,
//            date = date
//        )
//
//        val showingQuestionContent = FeeltalkApp.getQuestionIdOfShowingChatFragment()
//        val isAppShowing = FeeltalkApp.getAppRunning()
//
//        val saveResult = saveChatUseCase(chat2)
//        if (saveResult !is Resource.Success) {
//            // 채팅 저장이 실패하고
//            // 보이는 화면이 이 채팅의 채팅방일때
//            if (showingQuestionContent == questionContent) {
//                FcmNewChatObserver
//                    .getInstance()
//                    .setNewChat(chat2)
//                return@launch
//            }
//        }
//
//        if (showingQuestionContent == questionContent && isAppShowing) {
//            return@launch
//        }
//
//        val question = Question(
//            question = questionContent
//        )
//
//        val pendingIntent = NavDeepLinkBuilder(applicationContext)
////            .setGraph(R.navigation.overall_nav_graph)
//            .setDestination(R.id.chatFragment)
//            .setArguments(
//                bundleOf("selectedQuestion" to question)
//            )
//            .createPendingIntent()
//
//        showNotification(
//            title = data["title"] ?: "",
//            message = data["message"] ?: "",
//            notificationID = questionContent.toBytesInt(),
//            channelID = CHAT_CHANNEL_ID,
//            pendingIntent = pendingIntent
//        )
//    }
//
//    private fun handleCoupleRegistrationData(data: Map<String, String>) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//        } else {
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        }
//
//        CreateCoupleObserver
//            .getInstance()
//            .setCoupleCreated(true)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val appSettings = getAppSettingsUseCase().apply {
//                isNotificationUpdated = true
//            }
//            saveAppSettingsUseCase(appSettings)
//        }
//
//        val title = data["title"] ?: ""
//        val message = data["message"] ?: ""
//
//        val isAppRunning = FeeltalkApp.getAppRunning()
//        if (!isAppRunning) {
//            showNotification(
//                title = title,
//                message = message,
//                notificationID = COUPLE_REGISTRATION_CHANNEL_ID.toBytesInt(),
//                channelID = COUPLE_REGISTRATION_CHANNEL_ID,
//                pendingIntent = pendingIntent
//            )
//        }
//    }
//
//    private fun handleRequestKeyRestoringData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
//        val pendingIntent = NavDeepLinkBuilder(applicationContext)
////            .setGraph(R.navigation.overall_nav_graph)
//            .setDestination(R.id.keyRestoringAcceptFragment)
//            .createPendingIntent()
//
//        val title = data["title"] ?: ""
//        val message = data["message"] ?: ""
//
//        showNotification(
//            title = title,
//            message = message,
//            notificationID = REQUEST_KEY_RESTORING_CHANNEL_ID.toBytesInt(),
//            channelID = REQUEST_KEY_RESTORING_CHANNEL_ID,
//            pendingIntent = pendingIntent
//        )
//    }
//
//    private fun handleAcceptRequestKeyRestoringData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
//        AcceptRestoringKeysRequestObserver
//            .getInstance()
//            .setPartnerAccepted(true)
//
//        val isAppRunning = FeeltalkApp.getAppRunning()
//        if (!isAppRunning) {
//            showNotification(
//                title = data["title"] ?: "",
//                message = data["message"] ?: "",
//                notificationID = ACCEPT_KEY_RESTORING_CHANNEL_ID.toBytesInt(),
//                channelID = ACCEPT_KEY_RESTORING_CHANNEL_ID,
//                pendingIntent = null
//            )
//        }
//    }
//
//    private fun handleOtherCases(data: Map<String, String>) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//        } else {
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        }
//
//        val title = data["title"] ?: ""
//        val message = data["message"] ?: ""
//
//        showNotification(
//            title = title,
//            message = message,
//            channelID = ADVERTISING_CHANNEL_ID,
//            pendingIntent = pendingIntent
//        )
//    }
}