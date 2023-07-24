package com.clonect.feeltalk.new_presentation.notification

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.ChangeChatRoomStateUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.SendTextChatUseCase
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.MyChatRoomStateObserver
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.NewChatObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReplyReceiver: BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper
    @Inject
    lateinit var sendTextChatUseCase: SendTextChatUseCase
    @Inject
    lateinit var changeChatRoomStateUseCase: ChangeChatRoomStateUseCase

    @Inject
    lateinit var getAppSettingsUseCase: GetAppSettingsUseCase
    @Inject
    lateinit var saveAppSettingsUseCase: SaveAppSettingsUseCase


    override fun onReceive(context: Context?, intent: Intent?) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent) ?: return

        val title = remoteInput.getCharSequence(NotificationHelper.KEY_TEXT_REPLY, null)?.toString() ?: return
        val notificationID = NotificationHelper.CHAT_CHANNEL_ID.toBytesInt()
        infoLog("Notification text reply: $title, notificationID: $notificationID")

        sendTextChat(notificationID, title)
    }

    private fun sendTextChat(notificationID: Int, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            changeChatRoomState(true)
            
            when (val result = sendTextChatUseCase(message)) {
                is Resource.Success -> {
                    val textChat = result.data.run {
                        TextChat(
                            index = index,
                            pageNo = pageIndex,
                            chatSender = "me",
                            isRead = isRead,
                            createAt = createAt,
                            isSending = false,
                            message = message
                        )
                    }
                    NewChatObserver.getInstance().setNewChat(textChat)
                }
                is Resource.Error -> {
                    infoLog("Fail to send text message from notification: ${result.throwable.localizedMessage}")
                }
            }
            
            changeChatRoomState(false)
            withContext(Dispatchers.Main) {
                notificationHelper.cancelNotification(notificationID)

                val appSettings = getAppSettingsUseCase()
                appSettings.chatNotificationStack = 0
                saveAppSettingsUseCase(appSettings)
                infoLog("Notifications are dismissed (with Text Chat Reply)")
            }
        }
    }

    private suspend fun changeChatRoomState(isInChat: Boolean) {
        // 유저가 채팅을 키고있다면 패스
        if (MyChatRoomStateObserver.getInstance().getUserInChat()) 
            return

        when (val result = changeChatRoomStateUseCase(isInChat)) {
            is Resource.Success -> {
                infoLog("Success to change my chat room state")
            }
            is Resource.Error -> {
                infoLog("Fail to change my chat room state: ${result.throwable.localizedMessage}")
            }
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
}