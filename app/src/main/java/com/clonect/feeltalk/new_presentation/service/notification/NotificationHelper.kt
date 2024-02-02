package com.clonect.feeltalk.new_presentation.service.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetMySignalCacheFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetMySignalUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetPartnerSignalFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetPartnerSignalUseCase
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.bubble.BubbleActivity
import com.clonect.feeltalk.new_presentation.ui.util.toBytesInt
import com.navercorp.nid.NaverIdLoginSDK.applicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.util.Date

class NotificationHelper(
    private val getMySignalUseCase: GetMySignalUseCase,
    private val getPartnerSignalUseCase: GetPartnerSignalUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase
) {

    companion object {
        const val TYPE_CREATE_COUPLE = "createCouple"
        const val TYPE_CHAT_ROOM_STATE = "chatRoomStatusChange"
        const val TYPE_TEXT_CHATTING = "textChatting"
        const val TYPE_VOICE_CHATTING = "voiceChatting"
        const val TYPE_IMAGE_CHATTING = "imageChatting"
        const val TYPE_TODAY_QUESTION = "todayQuestion"
        const val TYPE_SIGNAL_CHATTING = "signalChatting"
        const val TYPE_QUESTION_CHATTING = "questionChatting"
        const val TYPE_ANSWER_CHATTING = "answerChatting"
        const val TYPE_ADD_CHALLENGE_CHATTING = "addChallengeChatting"
        const val TYPE_COMPLETE_CHALLENGE_CHATTING = "completeChallengeChatting"
        const val TYPE_PRESS_FOR_ANSWER_CHATTING = "pressForAnswerChatting"
        const val TYPE_RESET_PARTNER_PASSWORD_CHATTING = "resetPartnerPasswordChatting"

        const val TYPE_DELETE_CHALLENGE = "deleteChallenge"
        const val TYPE_MODIFY_CHALLENGE = "modifyChallenge"

        // TODO 수정 요망
        const val TYPE_ANSWER_QUESTION = "answerQuestion"


        const val KEY_TEXT_REPLY = "key_text_reply"

        const val NOTIFICATION_NORMAL_GROUP = "normal_group"
        const val NOTIFICATION_CHAT_GROUP = "chat_group"

        const val CHANEL_ID_CREATE_COUPLE = "feeltalk_create_couple_notification"
        const val CHANNEL_ID_CHAT = "feeltalk_chat_notification"
        const val CHANNEL_ID_TODAY_QUESTION = "feeltalk_today_question_notification"

        // TODO 수정 요망
        const val CHANNEL_ID_ADD_CHALLENGE = "feeltalk_add_challenge_notification"
        const val CHANNEL_ID_COMPLETE_CHALLENGE = "feeltalk_complete_challenge_notification"
        const val CHANNEL_ID_PRESS_FOR_ANSWER = "feeltalk_press_for_answer_notification"
        const val CHANNEL_ID_ANSWER_QUESTION = "feeltalk_answer_question_notification"


        const val CHANNEL_ID_DELETE_CHALLENGE = "feeltalk_delete_challenge_notification"
        const val CHANNEL_ID_MODIFY_CHALLENGE = "feeltalk_modify_challenge_notification"


        const val CHAT_SHORTCUT_ID = "chat_shortcut"
    }

    fun showNormalNotification(
        title: String,
        message: String,
        channelID: String,
        notificationID: Int = channelID.toBytesInt(),
        pendingIntent: PendingIntent? = null
    ) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val manager = NotificationManagerCompat.from(applicationContext)

        val notification = NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setColor(applicationContext.getColor(R.color.main_500))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup(NOTIFICATION_NORMAL_GROUP)
            .build()

        createNotificationChannel(channelID)
        manager.notify(notificationID, notification)
        groupNotifications(channelID)
    }


    fun showChatNotification(
        message: String
    ) = CoroutineScope(Dispatchers.Default).launch {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@launch
        }

        val notificationID = CHANNEL_ID_CHAT.toBytesInt()

        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.feeltalk_nav_graph)
            .setDestination(R.id.splashFragment)
            .setArguments(
                bundleOf("showChat" to true)
            )
            .createPendingIntent()

        val readResultIntent = Intent(applicationContext, NotificationReadReceiver::class.java)
        val readPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            readResultIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
            else PendingIntent.FLAG_ONE_SHOT
        )
        val readAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_notification_clear_all,
            applicationContext.getString(R.string.notification_read),
            readPendingIntent
        ).build()


        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel(applicationContext.getString(R.string.notification_reply))
            .build()
        val replyResultIntent = Intent(applicationContext, NotificationReplyReceiver::class.java)
        val replyPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            replyResultIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
            else PendingIntent.FLAG_ONE_SHOT
        )

        val replyAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            applicationContext.getString(R.string.notification_reply),
            replyPendingIntent
        ).run {
            addRemoteInput(remoteInput)
            build()
        }


        val bubble = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val target = Intent(applicationContext, BubbleActivity::class.java)
            val bubbleIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                target,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_ONE_SHOT
            )

            NotificationCompat.BubbleMetadata.Builder(bubbleIntent,
                IconCompat.createWithResource(applicationContext, R.drawable.ic_emotion_happy))
                .setDesiredHeight(600)
                .setSuppressNotification(false)
                .build()
        } else {
            null
        }

//        val partnerSignal = (getPartnerSignalUseCase() as? Resource.Success)?.data ?: Signal.Half
//        val partnerSignalRes = when (partnerSignal) {
//            Signal.Zero -> R.drawable.n_image_signal_0
//            Signal.Quarter -> R.drawable.n_image_signal_25
//            Signal.Half -> R.drawable.n_image_signal_50
//            Signal.ThreeFourth -> R.drawable.n_image_signal_75
//            Signal.One -> R.drawable.n_image_signal_100
//        }

//        val mySignal = (getMySignalUseCase() as? Resource.Success)?.data ?: Signal.Half
//        val mySignalRes = when (mySignal) {
//            Signal.Zero -> R.drawable.n_image_signal_0
//            Signal.Quarter -> R.drawable.n_image_signal_25
//            Signal.Half -> R.drawable.n_image_signal_50
//            Signal.ThreeFourth -> R.drawable.n_image_signal_75
//            Signal.One -> R.drawable.n_image_signal_100
//        }

        val partner = Person.Builder()
            .setName(applicationContext.getString(R.string.notification_partner))
//            .setIcon(IconCompat.createWithResource(applicationContext, partnerSignalRes))
            .build()

//        val me = Person.Builder()
//            .setName(applicationContext.getString(R.string.notification_me))
//            .setIcon(IconCompat.createWithResource(applicationContext, mySignalRes))
//            .build()

        val messageStyle = restoreMessagingStyle(notificationID)
            ?: NotificationCompat.MessagingStyle(partner)
                .setGroupConversation(false)
        messageStyle.addMessage(message, Date().time, partner)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_CHAT)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.n_ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(applicationContext.getColor(R.color.main_500))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setStyle(messageStyle)
            .setGroup(NOTIFICATION_CHAT_GROUP)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(deepLinkPendingIntent)
            .addAction(readAction)
            .addAction(replyAction)
            .setShortcutId(CHAT_SHORTCUT_ID)
            .setBubbleMetadata(bubble)
            .build()

        createNotificationChannel(CHANNEL_ID_CHAT)
        NotificationManagerCompat.from(applicationContext).notify(notificationID, notification)
        groupNotifications(CHANNEL_ID_CHAT)
    }


    private fun restoreMessagingStyle(notificationID: Int): NotificationCompat.MessagingStyle? {
        return (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .activeNotifications
            .find { it.id == notificationID }
            ?.notification
            ?.let { NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(it) }
    }

    private fun findActiveNotification(notificationId: Int): Notification? {
        return (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .activeNotifications
            .find {
                it.id == notificationId
            }?.notification
    }

    fun addChatReply(message: CharSequence, notificationID: Int, isReplySuccess: Boolean = false) = CoroutineScope(Dispatchers.Main).launch {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@launch
        }

        val activeNotification = findActiveNotification(notificationID) ?: return@launch
        val activeStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(activeNotification)

        val recoveredBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Notification.Builder.recoverBuilder(applicationContext, activeNotification)
        } else {
            cancelNotification(notificationID)
            return@launch
        }

//        val partnerSignal = (getPartnerSignalUseCase() as? Resource.Success)?.data ?: Signal.Half
//        val partnerSignalRes = when (partnerSignal) {
//            Signal.Zero -> R.drawable.n_image_signal_0
//            Signal.Quarter -> R.drawable.n_image_signal_25
//            Signal.Half -> R.drawable.n_image_signal_50
//            Signal.ThreeFourth -> R.drawable.n_image_signal_75
//            Signal.One -> R.drawable.n_image_signal_100
//        }
//
//        val mySignal = (getMySignalUseCase() as? Resource.Success)?.data ?: Signal.Half
//        val mySignalRes = when (mySignal) {
//            Signal.Zero -> R.drawable.n_image_signal_0
//            Signal.Quarter -> R.drawable.n_image_signal_25
//            Signal.Half -> R.drawable.n_image_signal_50
//            Signal.ThreeFourth -> R.drawable.n_image_signal_75
//            Signal.One -> R.drawable.n_image_signal_100
//        }

        val partner = android.app.Person.Builder()
            .setName(applicationContext.getString(R.string.notification_partner))
//            .setIcon(Icon.createWithResource(applicationContext, partnerSignalRes))
            .build()

        val me = android.app.Person.Builder()
            .setName(applicationContext.getString(R.string.notification_me))
//            .setIcon(Icon.createWithResource(applicationContext, mySignalRes))
            .build()

        val newStyle = Notification.MessagingStyle(partner)
            .setGroupConversation(false)

        activeStyle?.messages?.forEach {
            newStyle.addMessage(
                it.text.toString(),
                it.timestamp,
                if (it.person?.name == me.name) me
                else partner
            )
        }
        if (isReplySuccess) {
            newStyle.addMessage(message, System.currentTimeMillis(), me)
        }

        recoveredBuilder.style = newStyle
        NotificationManagerCompat.from(applicationContext).notify(notificationID, recoveredBuilder.build())
    }

    fun cancelNotification(notificationID: Int) {
        val manager = NotificationManagerCompat.from(applicationContext)
        manager.cancel(notificationID)
    }


    private fun groupNotifications(channelID: String) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val group =
            if (channelID == CHANNEL_ID_CHAT) NOTIFICATION_CHAT_GROUP
            else NOTIFICATION_NORMAL_GROUP

        val groupNotification = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setColor(applicationContext.getColor(R.color.main_500))
            .setAutoCancel(true)
            .setGroup(group)
            .setGroupSummary(true)
            .setOnlyAlertOnce(false)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
            .build()

        val manager = NotificationManagerCompat.from(applicationContext)
        manager.notify(group.toBytesInt(), groupNotification)
    }


    private fun createNotificationChannel(
        channelID: String,
        channelName: String = getChannelName(channelID),
        channelDescription: String = getChannelDescription(channelID)
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = NotificationManagerCompat.from(applicationContext)

            val channel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
            }
            manager.createNotificationChannel(channel)
        }
    }


    private fun String.toBytesInt(): Int {
        val bytes = encodeToByteArray()
        var result = 0
        for (i in bytes.indices) {
            result = result or (bytes[i].toInt() shl 8 * i)
        }
        return result
    }


    private fun getChannelName(channelID: String): String = when (channelID) {
        CHANEL_ID_CREATE_COUPLE -> "커플 등록"
        CHANNEL_ID_CHAT -> "채팅"
        CHANNEL_ID_TODAY_QUESTION -> "오늘의 질문"
        CHANNEL_ID_PRESS_FOR_ANSWER -> "답변 요청하기"
        CHANNEL_ID_ANSWER_QUESTION -> "연인의 답변"
        CHANNEL_ID_ADD_CHALLENGE -> "챌린지 만들기"
        CHANNEL_ID_DELETE_CHALLENGE -> "챌린지 삭제"
        CHANNEL_ID_MODIFY_CHALLENGE -> "챌린지 수정"
        CHANNEL_ID_COMPLETE_CHALLENGE -> "챌린지 완료"
        else -> "기타"
    }

    private fun getChannelDescription(channelID: String): String = when (channelID) {
        CHANEL_ID_CREATE_COUPLE -> "커플 등록"
        CHANNEL_ID_CHAT -> "채팅"
        CHANNEL_ID_TODAY_QUESTION -> "오늘의 질문"
        CHANNEL_ID_PRESS_FOR_ANSWER -> "답변 요청하기"
        CHANNEL_ID_ANSWER_QUESTION -> "연인의 답변"
        CHANNEL_ID_ADD_CHALLENGE -> "챌린지 만들기"
        CHANNEL_ID_DELETE_CHALLENGE -> "챌린지 삭제"
        CHANNEL_ID_MODIFY_CHALLENGE -> "챌린지 수정"
        CHANNEL_ID_COMPLETE_CHALLENGE -> "챌린지 완료"
        else -> "기타"
    }
}