package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.new_domain.model.chat.ChatType
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_presentation.service.notification_observer.NewChatObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase,
): ViewModel() {

    private val _latestPartnerChat = MutableStateFlow<String?>(null)
    val latestPartnerChat = _latestPartnerChat.asStateFlow()

    private var isArgumentsInit = true

    private val _showChatNavigation = MutableStateFlow(false)
    val showChatNavigation = _showChatNavigation.asStateFlow()

    init {
        collectNewChat()
    }

    fun toggleShowChatNavigation() {
        _showChatNavigation.value = _showChatNavigation.value.not()
    }

    fun setShowChatNavigation(showChat: Boolean) = viewModelScope.launch {
        if (isArgumentsInit) {
            isArgumentsInit = false
            _showChatNavigation.value = showChat

            val appSettings = getAppSettingsUseCase()
            appSettings.activeChatNotification = 0
            saveAppSettingsUseCase(appSettings)
        }
    }


    private fun collectNewChat() = viewModelScope.launch {
        NewChatObserver
            .getInstance()
            .newChat
            .collectLatest { newChat ->
                runCatching {
                    val message  = when (newChat?.type) {
                        ChatType.TextChatting -> {
                            val textChat = newChat as? TextChat ?: return@collectLatest
                            textChat.message
                        }
                        ChatType.VoiceChatting -> {
                            "(보이스 채팅)"
                        }
                        else -> return@collectLatest
                    }

                    _latestPartnerChat.value = message
                }.onFailure {
                    infoLog("collectNewChat(): ${it.localizedMessage}")
                }
            }
    }
}