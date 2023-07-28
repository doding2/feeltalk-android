package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.ChatType
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.usecase.chat.GetPartnerLastChatUseCase
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.NewChatObserver
import com.clonect.feeltalk.new_presentation.ui.activity.MainActivity
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val getPartnerLastChatUseCase: GetPartnerLastChatUseCase,
): ViewModel() {

    private val _partnerLastChat = MutableStateFlow<String?>(null)
    val partnerLastChat = _partnerLastChat.asStateFlow()

    private var isArgumentsInit = true

    private val _showChatNavigation = MutableStateFlow(false)
    val showChatNavigation = _showChatNavigation.asStateFlow()

    init {
        getPartnerLastChat()
        collectNewChat()
    }

    fun setShortcut(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("showChat", true)
        }

        val partner = android.app.Person.Builder()
            .setName(context.getString(R.string.notification_partner))
            .setIcon(IconCompat.createWithResource(context, R.drawable.image_my_default_profile).toIcon(context))
            .build()

        val shortcut = ShortcutInfo.Builder(context, NotificationHelper.CHAT_SHORTCUT_ID).run {
            setLongLived(true)
            setShortLabel(context.getString(R.string.app_name))
            setPerson(partner)
            setIcon(IconCompat.createWithResource(context, R.drawable.n_image_bubble).toIcon(context))
            setCategories(setOf(ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION))
            setIntent(intent)
            build()
        }

        val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
        shortcutManager.pushDynamicShortcut(shortcut)
    }
    
    private fun getPartnerLastChat() = viewModelScope.launch { 
        when (val result = getPartnerLastChatUseCase()) {
            is Resource.Success -> {
                _partnerLastChat.value = result.data.message
            }
            is Resource.Error -> {
                infoLog("연인의 가장 최근 채팅 가져오기 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun toggleShowChatNavigation() {
        _showChatNavigation.value = _showChatNavigation.value.not()
    }

    fun setShowChatNavigation(showChat: Boolean) = viewModelScope.launch {
        if (isArgumentsInit) {
            isArgumentsInit = false
            _showChatNavigation.value = showChat
        }
    }


    private fun collectNewChat() = viewModelScope.launch {
        NewChatObserver
            .getInstance()
            .newChat
            .collect { newChat ->
                runCatching {
                    if (newChat?.chatSender == "me") return@collect

                    val message  = when (newChat?.type) {
                        ChatType.TextChatting -> {
                            val textChat = newChat as? TextChat ?: return@collect
                            textChat.message
                        }
                        ChatType.VoiceChatting -> {
                            "(보이스 채팅)"
                        }
                        else -> return@collect
                    }

                    _partnerLastChat.value = message
                }.onFailure {
                    infoLog("collectNewChat(): ${it.localizedMessage}")
                }
            }
    }
}