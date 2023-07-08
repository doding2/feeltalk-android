package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.plusDayBy
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatType
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.model.chat.VoiceChat
import com.clonect.feeltalk.new_domain.usecase.chat.ChangeChatRoomStateUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.GetChatListUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.GetLastChatPageNoUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.SendTextChatUseCase
import com.clonect.feeltalk.new_presentation.service.notification_observer.NewChatObserver
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer.RecordingReplayer
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer.RecordingSampler
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer.VisualizerView
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val changeChatRoomStateUseCase: ChangeChatRoomStateUseCase,
    private val getLastChatPageNoUseCase: GetLastChatPageNoUseCase,
    private val getChatListUseCase: GetChatListUseCase,
    private val sendTextChatUseCase: SendTextChatUseCase
) : ViewModel() {

    private val _scrollToBottom = MutableSharedFlow<Boolean>()
    val scrollToBottom = _scrollToBottom.asSharedFlow()

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()


    private val _lastChatPageNo = MutableStateFlow(0L)
    val lastChatPageNo = _lastChatPageNo.asStateFlow()

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList = _chatList.asStateFlow()

    private val _textChat = MutableStateFlow("")
    val textChat = _textChat.asStateFlow()

    private val _expandChat = MutableStateFlow(false)
    val expandChat = _expandChat.asStateFlow()


    suspend fun changeChatRoomState(isInChat: Boolean) = withContext(Dispatchers.IO) {
        when (val result = changeChatRoomStateUseCase(isInChat)) {
            is Resource.Success -> {
                result.data
                infoLog("채팅 입장 상태 변경 성공")
            }
            is Resource.Error -> {
                infoLog("채팅 입장 상태 변경 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun getLastChatPageNo() = viewModelScope.launch {
        when (val result = getLastChatPageNoUseCase()) {
            is Resource.Success -> {
                val pageNo = result.data.pageNo
                _lastChatPageNo.value = pageNo
            }
            is Resource.Error -> {
                infoLog("가장 최근 채팅 페이지 가져오기 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun loadChatList(pageNo: Long = lastChatPageNo.value) = viewModelScope.launch {
        when (val result = getChatListUseCase(pageNo)) {
            is Resource.Success -> {
                val thisPageNo = result.data.page
                val newChatList = mutableListOf<Chat>()
                for (chatDto in result.data.chatting) {
                    val chat = when (chatDto.type) {
                        "text", "textChatting" -> {
                            chatDto.run {
                                TextChat(
                                    index = index,
                                    pageNo = thisPageNo,
                                    chatSender = if (mine) "me" else "partner",
                                    isRead = isRead,
                                    createAt = createAt,
                                    message = message ?: ""
                                )
                            }
                        }
                        else -> {
                            continue
                        }
                    }
                    newChatList.add(chat)
                }

                val chatList = chatList.value.toMutableList()
                chatList.addAll(newChatList)
                chatList.sortBy { it.index }
                _chatList.value = chatList
            }
            is Resource.Error -> {
                infoLog("가장 최근 채팅 페이지 가져오기 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun sendTextChat(onSend: () -> Unit) = viewModelScope.launch {
        val message = _textChat.value
        _textChat.value = ""
        onSend()

        when (val result = sendTextChatUseCase(message)) {
            is Resource.Success -> {
                val chat = result.data.run {
                    TextChat(
                        index = index,
                        pageNo = lastChatPageNo.value,
                        chatSender = "me",
                        isRead = isRead,
                        createAt = createAt,
                        message = message
                    )
                }

                _chatList.value = _chatList.value.toMutableList().apply {
                    add(chat)
                }
                delay(50)
                setScrollToBottom()
            }
            is Resource.Error -> {
                infoLog("텍스트 채팅 전송 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }


    fun collectFcmChat()  = viewModelScope.launch {
        NewChatObserver
            .getInstance()
            .newChat
            .collectLatest { newChat ->
                val chat = when (newChat?.type) {
                    ChatType.TextChatting -> {
                        val textChat = newChat as? TextChat ?: return@collectLatest
                        textChat
                    }
                    else -> return@collectLatest
                }

                _chatList.value = _chatList.value.toMutableList().apply {
                    add(chat)
                }
            }
    }

    fun clearChatList() {
        _chatList.value = emptyList()
        NewChatObserver.onCleared()
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
                chatSender = "partner",
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
                createAt = createAtYesterday,
                message = "허걱스"
            ),
            TextChat(
                index = 8,
                pageNo = 0,
                chatSender = "partner",
                isRead = true,
                createAt = createAtYesterday,
                message = "대박"
            ),
        )

        _chatList.value = newChatList
        setScrollToBottom()
    }




    /** Voice **/

    // 셋업
    private val _isVoiceSetupMode = MutableStateFlow(false)
    val isVoiceSetupMode = _isVoiceSetupMode.asStateFlow()

    // 보이스 녹음중
    private val _isVoiceRecordingMode = MutableStateFlow(false)
    val isVoiceRecordingMode = _isVoiceRecordingMode.asStateFlow()

    // 보이스 녹음 완료
    private val _isVoiceRecordingFinished = MutableStateFlow(false)
    val isVoiceRecordingFinished = _isVoiceRecordingFinished.asStateFlow()

    private val _voiceSampler = MutableStateFlow<RecordingSampler?>(null)
    val voiceSampler = _voiceSampler.asStateFlow()

    // 타이머
    private var recordTimer: Timer? = null
    private val _voiceRecordTime = MutableStateFlow(0L)
    val voiceRecordTime = _voiceRecordTime.asStateFlow()

    // 리플레이 재생중
    private var voiceReplayer: RecordingReplayer? = null
    private val _isVoiceRecordingReplaying = MutableStateFlow(false)
    val isVoiceRecordingReplaying = _isVoiceRecordingReplaying.asStateFlow()

    // 리플레이 재생 상태에서 리플레이 중단
    private val _isVoiceRecordingReplayPaused = MutableStateFlow(true)
    val isVoiceRecordingReplayPaused = _isVoiceRecordingReplayPaused.asStateFlow()

    // 리플레이 재생 완료
    private val _isVoiceRecordingReplayCompleted = MutableStateFlow(false)
    val isVoiceRecordingReplayCompleted = _isVoiceRecordingReplayCompleted.asStateFlow()


    fun sendVoiceChat(onComplete: () -> Unit) = viewModelScope.launch {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        VoiceChat(
            index = _chatList.value.size.toLong(),
            pageNo = 0,
            chatSender = "me",
            isRead = true,
            createAt = format.format(Date()),
            url = "voiceCache.wav",
        ).also {
            _chatList.value = _chatList.value
                .toMutableList()
                .apply {
                    add(it)
                }
        }
        onComplete()
        delay(50)
        setScrollToBottom()
    }


    fun setVoiceSetupMode(isSetup: Boolean) {
        _isVoiceSetupMode.value = isSetup
    }

    fun startVoiceRecording(context: Context, visualizerView: VisualizerView) {
        _isVoiceRecordingMode.value = true
        _isVoiceRecordingFinished.value = false
        _voiceSampler.value = RecordingSampler(context).apply {
            link(visualizerView)
            startRecording()
        }
        // 녹음 시간 기록하기
        _voiceRecordTime.value = 0
        recordTimer = Timer()
        recordTimer?.schedule(object: TimerTask() {
            override fun run() {
                _voiceRecordTime.value += 1000
            }
        }, 1000, 1000)
    }

    fun finishVoiceRecording() {
        _voiceSampler.value?.stopRecording()
        _isVoiceRecordingFinished.value = true
        recordTimer?.cancel()
    }

    fun cancelVoiceRecordingMode() {
        _isVoiceRecordingMode.value = false
        _isVoiceRecordingFinished.value = false
        recordTimer?.cancel()
        _voiceSampler.value?.stopRecording()
        _voiceSampler.value = null
    }

    fun startVoiceRecordingReplay(context: Context, visualizerView: VisualizerView) {
        _isVoiceRecordingReplayCompleted.value = false
        _isVoiceRecordingReplaying.value = true
        _isVoiceRecordingReplayPaused.value = false
        val voiceFile = _voiceSampler.value?.voiceRecordFile ?: return
        voiceReplayer?.stop()
        voiceReplayer = RecordingReplayer(context, voiceFile, visualizerView)
        voiceReplayer?.replay()

        _voiceRecordTime.value = 0
        recordTimer = Timer()
        recordTimer?.schedule(object: TimerTask() {
            override fun run() {
                if (voiceReplayer?.isReplaying == true) {
                    _voiceRecordTime.value += 100
                }
                if (voiceReplayer?.isCompleted == true) {
                    _isVoiceRecordingReplayCompleted.value = true
                    _isVoiceRecordingReplaying.value = false
                    _isVoiceRecordingReplayPaused.value = true
                    cancel()
                }
            }
        }, 100, 100)
    }

    fun stopVoiceRecordingReplay() {
        voiceReplayer?.stop()
        voiceReplayer = null
        recordTimer?.cancel()
        _isVoiceRecordingReplayCompleted.value = false
        _isVoiceRecordingReplaying.value = false
        _isVoiceRecordingReplayPaused.value = false
        _isVoiceRecordingReplayPaused.value = true
    }

    fun pauseVoiceRecordingReplay() {
        _isVoiceRecordingReplayPaused.value = true
        voiceReplayer?.pause()
    }

    fun resumeVoiceRecordingReplay() {
        _isVoiceRecordingReplayPaused.value = false
        voiceReplayer?.resume()
    }

}