package com.clonect.feeltalk.new_presentation.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.clonect.feeltalk.R
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.bubble.BubbleActivity
import com.navercorp.nid.NaverIdLoginSDK.applicationContext
import java.util.*

class NotificationHelper(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase
) {

    companion object {
        const val TYPE_CREATE_COUPLE = "createCouple"
        const val TYPE_CHAT_ROOM_STATE = "chatRoomStatusChange"
        const val TYPE_TEXT_CHATTING = "textChatting"
        const val TYPE_VOICE_CHATTING = "voiceChatting"

        const val KEY_TEXT_REPLY = "key_text_reply"

        const val NOTIFICATION_NORMAL_GROUP = "normal_group"
        const val NOTIFICATION_CHAT_GROUP = "chat_group"

        const val CHAT_CHANNEL_ID ="feeltalk_chat_notification"
        const val CREATE_COUPLE_CHANNEL_ID ="feeltalk_create_couple_notification"

        const val CHAT_SHORTCUT_ID = "chat_shortcut"
    }

    fun showNormalNotification(
        title: String,
        message: String,
        channelID: String,
        notificationID: Int = channelID.toBytesInt(),
        pendingIntent: PendingIntent?
    ) {
        val manager = NotificationManagerCompat.from(applicationContext)

        val notification = NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.n_ic_notification)
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
        title: String,
        message: String
    ) {
        val notificationID = CHAT_CHANNEL_ID.toBytesInt()

        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.feeltalk_nav_graph)
            .setDestination(R.id.mainNavigationFragment)
            .setArguments(
                bundleOf("showChat" to true)
            )
            .createPendingIntent()

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
                .build()
        } else {
            null
        }

        val partner = Person.Builder()
            .setName(applicationContext.getString(R.string.notification_partner))
            .setIcon(IconCompat.createWithResource(applicationContext, R.drawable.image_my_default_profile))
            .build()

        val me = Person.Builder()
            .setName(applicationContext.getString(R.string.notification_me))
            .setIcon(IconCompat.createWithResource(applicationContext, R.drawable.image_partner_default_profile))
            .build()

        val messageStyle = restoreMessagingStyle(notificationID)
            ?: NotificationCompat.MessagingStyle(me)
                .setGroupConversation(true)
        messageStyle.addMessage(message, Date().time, partner)

        val notification = NotificationCompat.Builder(applicationContext, CHAT_CHANNEL_ID)
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
            .addAction(replyAction)
            .setShortcutId(CHAT_SHORTCUT_ID)
            .setBubbleMetadata(bubble)
            .build()

        createNotificationChannel(CHAT_CHANNEL_ID)
        NotificationManagerCompat.from(applicationContext).notify(notificationID, notification)
        groupNotifications(CHAT_CHANNEL_ID)
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

    fun addChatReply(message: CharSequence, notificationID: Int, isReplySuccess: Boolean = false) {
        val activeNotification = findActiveNotification(notificationID) ?: return
        val activeStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(activeNotification)

        val recoveredBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Notification.Builder.recoverBuilder(applicationContext, activeNotification)
        } else {
            cancelNotification(notificationID)
            return
        }

        val partner = android.app.Person.Builder()
            .setName(applicationContext.getString(R.string.notification_partner))
            .setIcon(Icon.createWithResource(applicationContext, R.drawable.image_my_default_profile))
            .build()

        val me = android.app.Person.Builder()
            .setName(applicationContext.getString(R.string.notification_me))
            .setIcon(Icon.createWithResource(applicationContext, R.drawable.image_partner_default_profile))
            .build()

        val newStyle = Notification.MessagingStyle(me)
            .setGroupConversation(true)

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
        val group =
            if (channelID == CHAT_CHANNEL_ID) NOTIFICATION_CHAT_GROUP
            else NOTIFICATION_NORMAL_GROUP

        val groupNotification = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.drawable.n_ic_notification)
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
        CREATE_COUPLE_CHANNEL_ID -> "커플 등록"
        CHAT_CHANNEL_ID -> "채팅"
        else -> "기타"
    }

    private fun getChannelDescription(channelID: String): String = when (channelID) {
        CREATE_COUPLE_CHANNEL_ID -> "커플 등록"
        CHAT_CHANNEL_ID -> "채팅"
        else -> "기타"
    }
}