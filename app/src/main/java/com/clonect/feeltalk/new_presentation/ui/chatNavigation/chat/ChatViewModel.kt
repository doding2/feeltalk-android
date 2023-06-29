package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.plusDayBy
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.DividerChat
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer.RecordingSampler
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer.VisualizerView
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(

) : ViewModel() {

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList = _chatList.asStateFlow()

    private val _textChat = MutableStateFlow("")
    val textChat = _textChat.asStateFlow()

    private val _expandChat = MutableStateFlow(false)
    val expandChat = _expandChat.asStateFlow()

    private val _isVoiceSetupMode = MutableStateFlow(false)
    val isVoiceSetupMode = _isVoiceSetupMode.asStateFlow()

    private val _isVoiceRecordingMode = MutableStateFlow(false)
    val isVoiceRecordingMode = _isVoiceRecordingMode.asStateFlow()

    private val _isVoiceRecordingStopMode = MutableStateFlow(false)
    val isVoiceRecordingStopMode = _isVoiceRecordingStopMode.asStateFlow()

    private val _voiceSampler = MutableStateFlow<RecordingSampler?>(null)
    val voiceSampler = _voiceSampler.asStateFlow()

    private val _scrollToBottom = MutableSharedFlow<Boolean>()
    val scrollToBottom = _scrollToBottom.asSharedFlow()

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()


    init {
        initChatList()
    }

    fun sendTextChat(onComplete: () -> Unit) = viewModelScope.launch {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        TextChat(
            index = _chatList.value.size.toLong(),
            pageNo = 0,
            chatSender = "me",
            isRead = true,
            createAt = format.format(Date()),
            message = _textChat.value
        ).also {
            _chatList.value = _chatList.value
                .toMutableList()
                .apply {
                    add(it)
                }
        }
        _textChat.value = ""
        onComplete()
        delay(50)
        setScrollToBottom()
    }

    fun setScrollToBottom() = viewModelScope.launch {
        _scrollToBottom.emit(true)
    }


    fun setTextChat(message: String) {
        _textChat.value = message
    }


    fun toggleExpandChatMedia() {
        _expandChat.value = _expandChat.value.not()
    }
    fun setExpandChatMedia(isExpanded: Boolean) {
        _expandChat.value = isExpanded
    }


    fun setVoiceSetupMode(isSetup: Boolean) {
        _isVoiceSetupMode.value = isSetup
    }


    fun startVoiceRecording(context: Context, visualizerView: VisualizerView) {
        _isVoiceRecordingMode.value = true
        _isVoiceRecordingStopMode.value = false
        _voiceSampler.value = RecordingSampler(context).apply {
            setSamplingInterval(100)
            link(visualizerView)
            startRecording()
            infoLog("start recording. isRecording: $isRecording")
            setOnRecordTimeListener {
                infoLog("record time: $it")
            }
        }
    }
    fun stopVoiceRecording() {
        _voiceSampler.value?.stopRecording()
        _isVoiceRecordingStopMode.value = true
    }
    fun setVoiceRecordingMode(isRecording: Boolean) {
        _isVoiceRecordingMode.value = isRecording
        _isVoiceRecordingStopMode.value = false
    }


    fun setKeyboardUp(isUp: Boolean) {
        _isKeyboardUp.value = isUp
    }




    private fun initChatList() = viewModelScope.launch {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val createAtWeekAgo = format.format(Date().plusDayBy(-7))
        val createAtYesterday = format.format(Date().plusDayBy(-1))
        val createAtToday = format.format(Date())

        val newChatList = listOf<Chat>(
            TextChat(
                index = 0,
                pageNo = 0,
                chatSender = "me",
                isRead = true,
                createAt = createAtWeekAgo,
                message = "첫번째 메시지"
            ),
            TextChat(
                index = 1,
                pageNo = 0,
                chatSender = "partner",
                isRead = true,
                createAt = createAtWeekAgo,
                message = "두번째 메시지"
            ),
            TextChat(
                index = 3,
                pageNo = 0,
                chatSender = "me",
                isRead = false,
                createAt = createAtWeekAgo,
                message = "내 메시지"
            ),
            TextChat(
                index = 4,
                pageNo = 0,
                chatSender = "partner",
                isRead = false,
                createAt = createAtWeekAgo,
                message = "연인 메시지"
            ),
            TextChat(
                index = 5,
                pageNo = 0,
                chatSender = "me",
                isRead = true,
                createAt = createAtYesterday,
                message = "띵동"
            ),
            TextChat(
                index = 6,
                pageNo = 0,
                chatSender = "partner",
                isRead = true,
                createAt = createAtYesterday,
                message = "ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ"
            ),
            TextChat(
                index = 7,
                pageNo = 0,
                chatSender = "partner",
                isRead = true,
                createAt = createAtToday,
                message = "허걱"
            ),
        )

        _chatList.value = newChatList
            .groupBy {
                it.createAt.substringBefore("T")
            }.map {
                val divider = DividerChat(it.key)
                val list = it.value
                    .toMutableList()
                    .apply {
                        add(0, divider)
                    }
                list
            }.flatten()
        setScrollToBottom()
    }
}