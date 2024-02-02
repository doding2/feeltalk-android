package com.clonect.feeltalk.new_presentation.service

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.domain.usecase.user.GetUserIsActiveUseCase
import com.clonect.feeltalk.domain.usecase.user.SetUserIsActiveUseCase
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.CoupleChallengeDto
import com.clonect.feeltalk.new_domain.model.chat.AddChallengeChat
import com.clonect.feeltalk.new_domain.model.chat.AnswerChat
import com.clonect.feeltalk.new_domain.model.chat.CompleteChallengeChat
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_domain.model.chat.PokeChat
import com.clonect.feeltalk.new_domain.model.chat.QuestionChat
import com.clonect.feeltalk.new_domain.model.chat.ResetPartnerPasswordChat
import com.clonect.feeltalk.new_domain.model.chat.SignalChat
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.model.chat.VoiceChat
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.usecase.account.SetCoupleCreatedUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.AddPartnerChallengeCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.DeletePartnerChallengeCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.GetChallengeUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.AddNewChatCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.ChangePartnerChatRoomStateCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.PreloadImageUseCase
import com.clonect.feeltalk.new_domain.usecase.question.AnswerPartnerQuestionCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.question.ChangeTodayQuestionCacheUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetQuestionUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.ChangePartnerSignalCacheUseCase
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.CHANEL_ID_CREATE_COUPLE
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_ADD_CHALLENGE_CHATTING
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_ANSWER_CHATTING
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_ANSWER_QUESTION
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_CHAT_ROOM_STATE
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_COMPLETE_CHALLENGE_CHATTING
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_CREATE_COUPLE
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_DELETE_CHALLENGE
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_IMAGE_CHATTING
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_MODIFY_CHALLENGE
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_PRESS_FOR_ANSWER_CHATTING
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_QUESTION_CHATTING
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_RESET_PARTNER_PASSWORD_CHATTING
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_SIGNAL_CHATTING
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_TEXT_CHATTING
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_TODAY_QUESTION
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper.Companion.TYPE_VOICE_CHATTING
import com.clonect.feeltalk.new_presentation.ui.FeeltalkApp
import com.clonect.feeltalk.new_presentation.ui.activity.MainActivity
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@AndroidEntryPoint
class FirebaseCloudMessagingService: FirebaseMessagingService() {

    // Notification
    @Inject lateinit var notificationHelper: NotificationHelper

    // Account
    @Inject lateinit var setCoupleCreatedUseCase: SetCoupleCreatedUseCase

    // Question
    @Inject lateinit var getQuestionUseCase: GetQuestionUseCase
    @Inject lateinit var changeTodayQuestionCacheUseCase: ChangeTodayQuestionCacheUseCase
    @Inject lateinit var answerPartnerQuestionCacheUseCase: AnswerPartnerQuestionCacheUseCase

    // Challenge
    @Inject lateinit var getChallengeUseCase: GetChallengeUseCase
    @Inject lateinit var addPartnerChallengeCacheUseCase: AddPartnerChallengeCacheUseCase
    @Inject lateinit var deletePartnerChallengeCacheUseCase: DeletePartnerChallengeCacheUseCase
    @Inject lateinit var modifyPartnerChallengeCacheUseCase: DeletePartnerChallengeCacheUseCase

    // Signal
    @Inject lateinit var changePartnerSignalCacheUseCase: ChangePartnerSignalCacheUseCase

    // Chat
    @Inject lateinit var addNewChatCacheUseCase: AddNewChatCacheUseCase
    @Inject lateinit var changePartnerChatRoomStateCacheUseCase: ChangePartnerChatRoomStateCacheUseCase
    @Inject lateinit var preloadImageUseCase: PreloadImageUseCase

    // App Settings
    @Inject lateinit var getAppSettingsUseCase: GetAppSettingsUseCase
    @Inject lateinit var saveAppSettingsUseCase: SaveAppSettingsUseCase

    // Mixpanel
    @Inject lateinit var getMixpanelAPIUseCase: GetMixpanelAPIUseCase
    @Inject lateinit var getUserIsActiveUseCase: GetUserIsActiveUseCase
    @Inject lateinit var setUserIsActiveUseCase: SetUserIsActiveUseCase



    companion object {

        suspend fun getFcmToken() = suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance()
                .token
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }

        }

        suspend fun clearFcmToken() = suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance()
                .deleteToken()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
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

            val isAppRunning = FeeltalkApp.getAppScreenActive()
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
                TYPE_IMAGE_CHATTING -> handleImageChatData(data)
                TYPE_SIGNAL_CHATTING -> handleSignalChatData(data)
                TYPE_QUESTION_CHATTING -> handleQuestionChatData(data)
                TYPE_ANSWER_CHATTING -> handleAnswerChatData(data)
                TYPE_ADD_CHALLENGE_CHATTING -> handleAddChallengeChatData(data)
                TYPE_COMPLETE_CHALLENGE_CHATTING -> handleCompleteChallengeData(data)
                TYPE_RESET_PARTNER_PASSWORD_CHATTING -> handleResetPartnerPasswordChatData(data)
                TYPE_TODAY_QUESTION -> handleTodayQuestionData(data)
                TYPE_PRESS_FOR_ANSWER_CHATTING -> handlePressForAnswerChatData(data)
                TYPE_ANSWER_QUESTION -> handleAnswerQuestionData(data)
                TYPE_DELETE_CHALLENGE -> handleDeleteChallengeData(data)
                TYPE_MODIFY_CHALLENGE -> handleModifyChallengeData(data)
            }
        }
    }


    private fun handleCreateCoupleData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        setCoupleCreatedUseCase(true)

        if (FeeltalkApp.getAppScreenActive()) {
            return@launch
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(applicationContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)
        }

        notificationHelper.showNormalNotification(
            title = data["title"] ?: "연인 등록 완료",
            message = data["message"] ?: "연인 등록에 성공했습니다",
            channelID = CHANEL_ID_CREATE_COUPLE,
            pendingIntent = pendingIntent
        )
    }

    private fun handleChatRoomStateData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val isInChat = data["isInChat"]?.toBoolean() ?: return@launch
        changePartnerChatRoomStateCacheUseCase(isInChat)
    }

    private fun handleTextChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val message = data["message"] ?: return@launch
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch

        addNewChatCacheUseCase(
            TextChat(
                index = index,
                pageNo = pageIndex,
                chatSender = "partner",
                isRead = isRead,
                createAt = createAt,
                message = message
            )
        )

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = message
        )
    }

    private fun handleVoiceChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val url = data["url"] ?: return@launch
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch

        addNewChatCacheUseCase(
            VoiceChat(
                index = index,
                pageNo = pageIndex,
                chatSender = "partner",
                isRead = isRead,
                createAt = createAt,
                url = url
            )
        )

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = "(보이스 채팅)"
        )
    }

    private fun handleImageChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val url = data["url"] ?: return@launch
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch

        val imageBundle = preloadImageUseCase(index, url)
        addNewChatCacheUseCase(
            ImageChat(
                index = index,
                pageNo = pageIndex,
                chatSender = "partner",
                isRead = isRead,
                createAt = createAt,
                url = url,
                file = imageBundle.first,
                width = imageBundle.second,
                height = imageBundle.third
            )
        )

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = "(이미지 채팅)"
        )
    }

    private fun handleSignalChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val signal = data["signal"]?.toInt() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch

        addNewChatCacheUseCase(
            SignalChat(
                index = index,
                pageNo = pageIndex,
                chatSender = "partner",
                isRead = isRead,
                createAt = createAt,
                signal = when (signal) {
                    0 -> Signal.Zero
                    25 -> Signal.Quarter
                    50 -> Signal.Half
                    75 -> Signal.ThreeFourth
                    100 -> Signal.One
                    else -> return@launch
                }
            )
        )

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = "(시그널 채팅)"
        )
    }

    private fun handleQuestionChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch
        val coupleQuestion = data["coupleQuestion"] ?: return@launch
        val questionIndex = Gson().fromJson(coupleQuestion, JsonObject::class.java).get("index").asLong

        val question = (getQuestionUseCase(questionIndex) as? Resource.Success)?.data
        if (question != null) {
            addNewChatCacheUseCase(
                QuestionChat(
                    index = index,
                    pageNo = pageIndex,
                    chatSender = "partner",
                    isRead = isRead,
                    createAt = createAt,
                    question = question
                )
            )
        }

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = "질문 채팅"
        )
    }

    private fun handleAnswerChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch
        val coupleQuestion = data["coupleQuestion"] ?: return@launch
        val questionIndex = Gson().fromJson(coupleQuestion, JsonObject::class.java).get("index").asLong

        val question = (getQuestionUseCase(questionIndex) as? Resource.Success)?.data
        if (question != null) {
            addNewChatCacheUseCase(
                AnswerChat(
                    index = index,
                    pageNo = pageIndex,
                    chatSender = "partner",
                    isRead = isRead,
                    createAt = createAt,
                    question = question
                )
            )
        }

        if (getAppRunning() && question != null) {
            answerPartnerQuestionCacheUseCase(question)
        }

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = "답변 채팅"
        )
    }

    private fun handlePressForAnswerChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch
        val coupleQuestion = data["coupleQuestion"]?.toLong() ?: return@launch

        addNewChatCacheUseCase(
            PokeChat(
                index = index,
                pageNo = pageIndex,
                chatSender = "partner",
                isRead = isRead,
                createAt = createAt,
                questionIndex = coupleQuestion
            )
        )

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = "콕찌르기 채팅"
        )
    }

    private fun handleTodayQuestionData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val title = data["title"] ?: return@launch
        val message = data["message"] ?: return@launch
        val index = data["index"]?.toLong() ?: return@launch

        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.feeltalk_nav_graph)
            .setDestination(R.id.splashFragment)
            .setArguments(
                bundleOf(
                    "questionIndex" to index,
                    "isTodayQuestion" to true
                )
            )
            .createPendingIntent()

        if (getAppRunning()) {
            val todayQuestion = (getQuestionUseCase(index) as? Resource.Success)?.data
            if (todayQuestion != null) {
                changeTodayQuestionCacheUseCase(todayQuestion)
            }
        }

        notificationHelper.showNormalNotification(
            title = title,
            message = message,
            channelID = NotificationHelper.CHANNEL_ID_TODAY_QUESTION,
            pendingIntent = deepLinkPendingIntent
        )
    }

    private fun handleAnswerQuestionData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val title = data["title"] ?: return@launch
        val message = data["message"] ?: return@launch
        val index = data["index"]?.toLong() ?: return@launch

        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.feeltalk_nav_graph)
            .setDestination(R.id.splashFragment)
            .setArguments(
                bundleOf(
                    "questionIndex" to index,
                    "isTodayQuestion" to false
                )
            )
            .createPendingIntent()

        if (getAppRunning()) {
            val question = (getQuestionUseCase(index) as? Resource.Success)?.data
            if (question != null) {
                answerPartnerQuestionCacheUseCase(question)
            }
        }

        notificationHelper.showNormalNotification(
            title = title,
            message = message,
            channelID = NotificationHelper.CHANNEL_ID_ANSWER_QUESTION,
            notificationID = System.currentTimeMillis().toInt(),
            pendingIntent = deepLinkPendingIntent
        )
    }

    private fun handleAddChallengeChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch
        val coupleChallengeJson = data["coupleChallenge"] ?: return@launch
        val coupleChallenge = Gson().fromJson(coupleChallengeJson, CoupleChallengeDto::class.java)

        val challengeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val challenge = Challenge(
            index = coupleChallenge.index,
            title = coupleChallenge.challengeTitle,
            body = coupleChallenge.challengeBody,
            deadline = challengeFormat.parse(coupleChallenge.deadline) ?: Date(),
            owner = coupleChallenge.creator,
            isCompleted = false,
            isNew = true
        )
        addNewChatCacheUseCase(
            AddChallengeChat(
                index = index,
                pageNo = pageIndex,
                chatSender = "partner",
                isRead = isRead,
                createAt = createAt,
                challenge = challenge
            )
        )

        if (getAppRunning()) {
            addPartnerChallengeCacheUseCase(challenge)
        }

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = "챌린지 추가 채팅"
        )
    }

    private fun handleDeleteChallengeData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        if (getAppRunning()) {
            val challenge = (getChallengeUseCase(index) as? Resource.Success)?.data ?: return@launch
            deletePartnerChallengeCacheUseCase(challenge)
        }
    }

    private fun handleModifyChallengeData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val title = data["title"]
        val message = data["message"]
        val index = data["index"]?.toLong() ?: return@launch
        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.feeltalk_nav_graph)
            .setDestination(R.id.splashFragment)
            .setArguments(
                bundleOf(
                    "challengeIndex" to index,
                )
            )
            .createPendingIntent()

        if (getAppRunning()) {
            val challenge = (getChallengeUseCase(index) as? Resource.Success)?.data
            if (challenge != null && !challenge.isCompleted) {
                modifyPartnerChallengeCacheUseCase(challenge)
            }
        }

        notificationHelper.showNormalNotification(
            title = title ?: "연인이 챌린지를 수정했어요",
            message = message ?: "앱에 들어와서 확인해보세요",
            channelID = NotificationHelper.CHANNEL_ID_ADD_CHALLENGE,
            notificationID = System.currentTimeMillis().toInt(),
            pendingIntent = deepLinkPendingIntent
        )
    }

    private fun handleCompleteChallengeData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch
        val coupleChallengeJson = data["coupleChallenge"] ?: return@launch
        val coupleChallenge = Gson().fromJson(coupleChallengeJson, CoupleChallengeDto::class.java)

        val challengeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val challenge = Challenge(
            index = coupleChallenge.index,
            title = coupleChallenge.challengeTitle,
            body = coupleChallenge.challengeBody,
            deadline = challengeFormat.parse(coupleChallenge.deadline) ?: Date(),
            owner = coupleChallenge.creator,
            isCompleted = true,
            completeDate = Date()
        )
        addNewChatCacheUseCase(
            CompleteChallengeChat(
                index = index,
                pageNo = pageIndex,
                chatSender = "partner",
                isRead = isRead,
                createAt = createAt,
                challenge = challenge
            )
        )

        if (getAppRunning()) {
            deletePartnerChallengeCacheUseCase(challenge.copy(isCompleted = false))
            addPartnerChallengeCacheUseCase(challenge.copy(isCompleted = true))
        }

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = "챌린지 완료 채팅"
        )
    }

    private fun handleResetPartnerPasswordChatData(data: Map<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        val index = data["index"]?.toLong() ?: return@launch
        val pageIndex = data["pageIndex"]?.toLong() ?: 0
        val isRead = data["isRead"]?.toBoolean() ?: return@launch
        val createAt = data["createAt"] ?: return@launch

        addNewChatCacheUseCase(
            ResetPartnerPasswordChat(
                index = index,
                pageNo = pageIndex,
                chatSender = "partner",
                isRead = isRead,
                createAt = createAt,
            )
        )

        if (FeeltalkApp.getAppScreenActive() && FeeltalkApp.getUserInChat()) {
            return@launch
        }

        notificationHelper.showChatNotification(
            message = "잠금 해제 요청 채팅"
        )
    }


    private fun getAppRunning(): Boolean {
        val activityManager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == applicationContext.packageName) {
                    return true
                }
            }
        }
        return false
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