package com.clonect.feeltalk.new_presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.usecase.chat.ChangeChatRoomStateUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.MyChatRoomStateObserver
import com.clonect.feeltalk.new_presentation.ui.util.toBytesInt
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReadReceiver: BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper
    @Inject
    lateinit var changeChatRoomStateUseCase: ChangeChatRoomStateUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        infoLog("Notification chat read action")

        CoroutineScope(Dispatchers.IO).launch {
            notificationHelper.cancelNotification(NotificationHelper.CHANNEL_ID_CHAT.toBytesInt())
            changeChatRoomState(true)
            changeChatRoomState(false)
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
}