package com.clonect.feeltalk.new_presentation.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.clonect.feeltalk.R
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.navercorp.nid.NaverIdLoginSDK.applicationContext
import java.text.SimpleDateFormat
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
        const val KEY_NOTIFICATION_ID = "key_notification_id"

        const val NOTIFICATION_GROUP = "group"

        const val CHAT_CHANNEL_ID ="feeltalk_chat_notification"
        const val CREATE_COUPLE_CHANNEL_ID ="feeltalk_create_couple_notification"
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
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setColor(ContextCompat.getColor(applicationContext, R.color.white))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup(NOTIFICATION_GROUP)
            .build()

        createNotificationChannel(channelID)
        manager.notify(notificationID, notification)
        groupNotifications(channelID)
    }


    fun showChatNotification(
        title: String,
        message: String
    ) {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
        else PendingIntent.FLAG_ONE_SHOT
        val dismissResultIntent = Intent(applicationContext, NotificationDismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            dismissResultIntent,
            flag
        )

        val deepLinkPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.feeltalk_nav_graph)
            .setDestination(R.id.mainNavigationFragment)
            .setArguments(
                bundleOf("showChat" to true)
            )
            .createPendingIntent()

        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel("답장")
            .build()
        val replyResultIntent = Intent(applicationContext, NotificationReplyReceiver::class.java)
        val replyPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            replyResultIntent,
            flag
        )

        val replyAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            "답장",
            replyPendingIntent
        ).run {
            addRemoteInput(remoteInput)
            build()
        }

        val appSettings = getAppSettingsUseCase().apply {
            chatNotificationStack += 1
        }
        saveAppSettingsUseCase(appSettings)
        val stackCount = appSettings.chatNotificationStack
        val stackCountStr = if (stackCount <= 1) "" else "$stackCount"

        val format = SimpleDateFormat("a h:mm", Locale.getDefault())

        val customView = RemoteViews(applicationContext.packageName, R.layout.notification_chat).apply {
            setTextViewText(R.id.tv_title, title)
            setTextViewText(R.id.tv_message, message)
            setTextViewText(R.id.tv_time, format.format(Date()))
            setTextViewText(R.id.tv_stack_count, stackCountStr)
        }

        val customViewExpanded = RemoteViews(applicationContext.packageName, R.layout.notification_chat_expanded).apply {
            setTextViewText(R.id.tv_title, title)
            setTextViewText(R.id.tv_message, message)
            setTextViewText(R.id.tv_stack_count, stackCountStr)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHAT_CHANNEL_ID)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(applicationContext.getColor(R.color.main_500))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setGroup(NOTIFICATION_GROUP)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(message)
            .setCustomContentView(customView)
            .setCustomBigContentView(customViewExpanded)
            .setContentIntent(deepLinkPendingIntent)
            .addAction(replyAction)
            .setDeleteIntent(dismissPendingIntent)
            .build()

        val notificationID = CHAT_CHANNEL_ID.toBytesInt()

        createNotificationChannel(CHAT_CHANNEL_ID)
        NotificationManagerCompat.from(applicationContext).notify(notificationID, notification)
        groupNotifications(CHAT_CHANNEL_ID)
    }

    private fun groupNotifications(channelID: String) {
        val groupNotification = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP)
            .setGroupSummary(true)
            .setOnlyAlertOnce(true)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
            .build()

        val manager = NotificationManagerCompat.from(applicationContext)
        manager.notify(NOTIFICATION_GROUP.toBytesInt(), groupNotification)
    }


    fun cancelNotification(notificationID: Int) {
        val manager = NotificationManagerCompat.from(applicationContext)
        manager.cancel(notificationID)
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
                setShowBadge(true)
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