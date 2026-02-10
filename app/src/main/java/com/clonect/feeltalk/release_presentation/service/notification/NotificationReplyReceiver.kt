package com.clonect.feeltalk.release_presentation.service.notification

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.chat.Chat
import com.clonect.feeltalk.release_domain.model.chat.TextChat
import com.clonect.feeltalk.release_domain.usecase.chat.AddNewChatCacheUseCase
import com.clonect.feeltalk.release_domain.usecase.chat.ChangeMyChatRoomStateUseCase
import com.clonect.feeltalk.release_domain.usecase.chat.GetMyChatRoomStateCacheUseCase
import com.clonect.feeltalk.release_domain.usecase.chat.SendTextChatUseCase
import com.clonect.feeltalk.release_presentation.ui.util.toBytesInt
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReplyReceiver: BroadcastReceiver() {

    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var addNewChatCacheUseCase: AddNewChatCacheUseCase
    @Inject lateinit var sendTextChatUseCase: SendTextChatUseCase
    @Inject lateinit var changeMyChatRoomStateUseCase: ChangeMyChatRoomStateUseCase
    @Inject lateinit var getMyChatRoomStateCacheUseCase: GetMyChatRoomStateCacheUseCase


    override fun onReceive(context: Context?, intent: Intent?) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent) ?: return

        val title = remoteInput.getCharSequence(NotificationHelper.KEY_TEXT_REPLY, null)?.toString() ?: return
        val notificationID = NotificationHelper.CHANNEL_ID_CHAT.toBytesInt()
        infoLog("Notification text reply action: $title")

        sendTextChat(notificationID, title)
    }

    private fun sendTextChat(notificationID: Int, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            changeChatRoomState(true)
            
            val success = when (val result = sendTextChatUseCase(message)) {
                is Resource.Success -> {
                    val textChat = result.data.run {
                        TextChat(
                            index = index,
                            pageNo = pageIndex,
                            chatSender = "me",
                            isRead = isRead,
                            createAt = createAt,
                            sendState = Chat.ChatSendState.Completed,
                            message = message
                        )
                    }
                    addNewChatCacheUseCase(textChat)
                    true
                }
                is Resource.Error -> {
                    infoLog("Fail to send text message from notification: ${result.throwable.localizedMessage}")
                    false
                }
            }
            
            changeChatRoomState(false)
            withContext(Dispatchers.Main) {
                notificationHelper.addChatReply(
                    message = message,
                    notificationID = notificationID,
                    isReplySuccess = success
                )
            }
        }
    }

    private suspend fun changeChatRoomState(isInChat: Boolean) {
        // 유저가 채팅을 키고있다면 패스
        if (getMyChatRoomStateCacheUseCase()) return

        when (val result = changeMyChatRoomStateUseCase(isInChat)) {
            is Resource.Success -> {
                infoLog("Success to change my chat room state")
            }
            is Resource.Error -> {
                infoLog("Fail to change my chat room state: ${result.throwable.localizedMessage}")
            }
        }
    }

}