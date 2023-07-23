package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatType
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.model.chat.VoiceChat
import com.clonect.feeltalk.new_domain.model.page.PageEvents
import com.clonect.feeltalk.new_domain.usecase.chat.ChangeChatRoomStateUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.GetPagingChatUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.SendTextChatUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.SendVoiceChatUseCase
import com.clonect.feeltalk.new_presentation.service.notification_observer.NewChatObserver
import com.clonect.feeltalk.new_presentation.service.notification_observer.PartnerChatRoomStateObserver
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer.RecordingReplayer
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer.RecordingSampler
import com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat.audioVisualizer.VisualizerView
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getPagingChatUseCase: GetPagingChatUseCase,
    private val changeChatRoomStateUseCase: ChangeChatRoomStateUseCase,
    private val sendTextChatUseCase: SendTextChatUseCase,
    private val sendVoiceChatUseCase: SendVoiceChatUseCase,
) : ViewModel() {

    private val job = MutableStateFlow<Job?>(null)

    private val _isUserInChat = MutableStateFlow<Boolean?>(null)
    val isUserInChat = _isUserInChat.asStateFlow()

    private val _isPartnerInChat = MutableStateFlow<Boolean?>(null)
    val isPartnerInChat = _isPartnerInChat.asStateFlow()

    private val _expandChat = MutableStateFlow(false)
    val expandChat = _expandChat.asStateFlow()

    private val _isUserInBottom = MutableStateFlow(true)
    val isUserInBottom = _isUserInBottom.asStateFlow()

    private val _scrollToBottom = MutableSharedFlow<Boolean>()
    val scrollToBottom = _scrollToBottom.asSharedFlow()

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()


    init {
        collectNewChat()
        collectPartnerChatRoomState()
    }

    fun setKeyboardUp(isUp: Boolean) {
        _isKeyboardUp.value = isUp
    }

    fun toggleExpandChatMedia() {
        _expandChat.value = _expandChat.value.not()
    }

    fun setExpandChatMedia(isExpanded: Boolean) {
        _expandChat.value = isExpanded
    }


    fun setUserInBottom(isInBottom: Boolean) {
        _isUserInBottom.value = isInBottom
    }

    fun setScrollToBottom() = viewModelScope.launch {
        _scrollToBottom.emit(true)
    }

    fun cancelJob() = viewModelScope.launch {
        job.value?.job
    }

    fun setJob(job: Job) {
        this.job.value = job
    }

    fun setTextChat(message: String) {
        _textChat.value = message
    }


    /** Page Modification **/
    private val pageModificationEvents = MutableStateFlow<List<PageEvents<Chat>>>(emptyList())

    private fun applyPageModification(paging: PagingData<Chat>, event: PageEvents<Chat>): PagingData<Chat> {
        return when (event) {
            is PageEvents.Edit -> {
                paging.map {
                    return@map if (it.index == event.item.index)
                        it.copy(event.item)
                    else
                        it
                }
            }
            is PageEvents.Remove -> {
                paging.filter { it.index != event.item.index }
            }
            is PageEvents.InsertItemFooter -> {
                paging.insertFooterItem(item = event.item)
            }
            is PageEvents.InsertItemHeader -> {
                paging.insertHeaderItem(item = event.item)
            }
        }
    }

    fun modifyPage(event: PageEvents<Chat>) {
        pageModificationEvents.value += event
    }


    /** Text Chat **/

    val pagingChat: Flow<PagingData<Chat>> = getPagingChatUseCase()
        .cachedIn(viewModelScope)
        .combine(pageModificationEvents) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyPageModification(acc, event)
            }
        }

    private val _textChat = MutableStateFlow("")
    val textChat = _textChat.asStateFlow()

    suspend fun changeChatRoomState(isInChat: Boolean) = withContext(Dispatchers.IO) {
        if (isUserInChat.value == isInChat) return@withContext

        when (val result = changeChatRoomStateUseCase(isInChat)) {
            is Resource.Success -> {
                _isUserInChat.value = isInChat
            }
            is Resource.Error -> {
                infoLog("채팅 입장 상태 변경 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun sendTextChat(onStart: () -> Unit = {}, onEnd: () -> Unit = {}) = viewModelScope.launch {
        val message = _textChat.value
        _textChat.value = ""
        onStart()

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val now = Date()

        val loadingTextChat = TextChat(
            index = now.time,
            pageNo = 0,
            chatSender = "me",
            isRead = true,
            createAt = format.format(now),
            isSending = true,
            message = message
        )
        launch {
            modifyPage(PageEvents.InsertItemFooter(loadingTextChat))

            delay(50)
            setScrollToBottom()
        }

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

                modifyPage(PageEvents.Remove(loadingTextChat))
                modifyPage(PageEvents.InsertItemFooter(textChat))
            }
            is Resource.Error -> {
                infoLog("텍스트 채팅 전송 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                modifyPage(PageEvents.Remove(loadingTextChat))
            }
        }
    }


    private fun collectNewChat() = viewModelScope.launch {
        NewChatObserver
            .getInstance()
            .newChat
            .collectLatest { newChat ->
                runCatching {
                    val chat = when (newChat?.type) {
                        ChatType.TextChatting -> {
                            val textChat = newChat as? TextChat ?: return@collectLatest
                            textChat
                        }
                        ChatType.VoiceChatting -> {
                            val voiceChat = newChat as? VoiceChat ?: return@collectLatest
                            voiceChat
                        }
                        else -> return@collectLatest
                    }

                    modifyPage(PageEvents.InsertItemFooter(chat))

                    if (isUserInBottom.value) {
                        delay(50)
                        setScrollToBottom()
                    }
                }.onFailure {
                    infoLog("collectNewChat(): ${it.localizedMessage}")
                }
            }
    }

    private fun collectPartnerChatRoomState() = viewModelScope.launch {
        PartnerChatRoomStateObserver
            .getInstance()
            .isInChat
            .collectLatest { isInChat ->
                runCatching {
                    _isPartnerInChat.value = isInChat
                }.onFailure {
                    infoLog("collectPartnerChatRoomState(): ${it.localizedMessage}")
                }
            }
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


    fun sendVoiceChat(context: Context, onSend: () -> Unit) = viewModelScope.launch {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val now = Date()

        val voiceCacheFile = File(context.cacheDir, Constants.VOICE_CACHE_FILE_NAME)
        if (!voiceCacheFile.exists() || !voiceCacheFile.canRead()) {
            infoLog("보이스 캐시 파일을 읽을 수 없습니다.")
            onSend()
            return@launch
        }
        val voiceFile = voiceCacheFile.copyTo(
            target = File(context.cacheDir, "${now.time}.wav"),
            overwrite = true
        )

        val loadingVoiceChat = VoiceChat(
            index = now.time,
            pageNo = 0,
            chatSender = "me",
            isRead = true,
            createAt = format.format(Date()),
            url = "index",
        )

        launch {
            modifyPage(PageEvents.InsertItemFooter(loadingVoiceChat))

            delay(50)
            setScrollToBottom()
        }
        onSend()

        when (val result = sendVoiceChatUseCase(voiceFile)) {
            is Resource.Success -> {
                val voiceChat = result.data.run {

                    withContext(Dispatchers.IO) {
                        val file = voiceFile.copyTo(
                            target = File(context.cacheDir, "${index}.wav"),
                            overwrite = true
                        )
                        voiceFile.delete()
                    }

                    VoiceChat(
                        index = index,
                        pageNo = pageIndex,
                        chatSender = "me",
                        isRead = isRead,
                        createAt = createAt,
                        isSending = false,
                        url = "index"
                    )
                }

                modifyPage(PageEvents.Remove(loadingVoiceChat))
                modifyPage(PageEvents.InsertItemFooter(voiceChat))
            }
            is Resource.Error -> {
                infoLog("보이스 채팅 전송 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                modifyPage(PageEvents.Remove(loadingVoiceChat))
            }
        }
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