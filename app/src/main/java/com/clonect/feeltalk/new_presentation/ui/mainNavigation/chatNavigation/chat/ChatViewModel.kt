package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.chat

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertFooterItem
import androidx.paging.insertHeaderItem
import androidx.paging.insertSeparators
import androidx.paging.map
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.PageEvents
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatType
import com.clonect.feeltalk.new_domain.model.chat.DividerChat
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_domain.model.chat.QuestionChat
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.model.chat.VoiceChat
import com.clonect.feeltalk.new_domain.usecase.chat.ChangeChatRoomStateUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.GetPagingChatUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.SendTextChatUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.SendVoiceChatUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.MyChatRoomStateObserver
import com.clonect.feeltalk.new_presentation.notification.observer.NewChatObserver
import com.clonect.feeltalk.new_presentation.notification.observer.PartnerChatRoomStateObserver
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.chat.audioVisualizer.RecordingReplayer
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.chat.audioVisualizer.RecordingSampler
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.chat.audioVisualizer.VisualizerView
import com.clonect.feeltalk.new_presentation.ui.util.toBitmap
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
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

    fun cancelJob() = viewModelScope.launch {
        job.value?.cancel()
    }

    fun setJob(job: Job) {
        this.job.value = job
    }

    fun setTextChat(message: String) {
        _textChat.value = message
    }


    /** Pagination **/
    private val pageModificationEvents = MutableStateFlow<List<PageEvents<Chat>>>(emptyList())

    private fun applyPageModification(paging: PagingData<Chat>, event: PageEvents<Chat>): PagingData<Chat> {
        return when (event) {
            is PageEvents.Edit -> {
                paging.map {
                    return@map if (it.index == event.item.index)
                        event.item
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

    private fun insertLoadingChat(chat: Chat) {
        pageModificationEvents.value += PageEvents.InsertItemFooter(chat)
    }

    private fun insertCompleteChat(chat: Chat) {
        val event = PageEvents.InsertItemFooter(chat)
        if (event in pageModificationEvents.value) return

        val firstSendingChatIndex = pageModificationEvents.value.indexOfFirst { it.item.sendState == Chat.ChatSendState.Sending }
        if (firstSendingChatIndex != -1) {
            pageModificationEvents.value = pageModificationEvents.value.toMutableList().apply {
                add(firstSendingChatIndex, event)
            }
        } else {
            pageModificationEvents.value += event
        }
    }

    fun removeLoadingChat(chat: Chat) {
        pageModificationEvents.value -= PageEvents.InsertItemFooter(chat)
    }

    private fun editLoadingChat(chat: Chat) {
        pageModificationEvents.value += PageEvents.Edit(chat)
    }

    fun cancelFailedChat(chat: Chat) {
        pageModificationEvents.value += PageEvents.Remove(chat)
    }

    private fun PagingData<Chat>.insertDividerChat(): PagingData<Chat> {
        return insertSeparators { before, after ->
            val beforeCreate = before?.createAt?.substringBefore("T")
            val afterCreate = after?.createAt?.substringBefore("T")

            return@insertSeparators if (beforeCreate.isNullOrBlank() && !afterCreate.isNullOrBlank()) {
                DividerChat(afterCreate)
            } else if (!beforeCreate.isNullOrBlank() && !afterCreate.isNullOrBlank() && beforeCreate != afterCreate) {
                DividerChat(afterCreate)
            } else {
                null
            }
        }
    }

    /** Text Chat **/

    val pagingChat: Flow<PagingData<Chat>> = getPagingChatUseCase()
        .cachedIn(viewModelScope)
        .combine(pageModificationEvents) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyPageModification(acc, event)
            }.insertDividerChat()
        }

    private val _textChat = MutableStateFlow("")
    val textChat = _textChat.asStateFlow()

    suspend fun changeChatRoomState(isInChat: Boolean) = withContext(Dispatchers.IO) {
        if (isUserInChat.value == isInChat) return@withContext

        when (val result = changeChatRoomStateUseCase(isInChat)) {
            is Resource.Success -> {
                MyChatRoomStateObserver.getInstance().setUserInChat(isInChat)
                _isUserInChat.value = isInChat
            }
            is Resource.Error -> {
                infoLog("채팅 입장 상태 변경 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun sendTextChat(retryChat: TextChat? = null, onStart: () -> Unit = {}) = viewModelScope.launch {
        var message = retryChat?.message
        if (message == null) {
            message = _textChat.value
            _textChat.value = ""
        }
        if (message.isEmpty()) return@launch
        onStart()

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val now = Date()

        val loadingTextChat = TextChat(
            index = now.time,
            pageNo = 0,
            chatSender = "me",
            isRead = true,
            createAt = format.format(now),
            sendState = Chat.ChatSendState.Sending,
            message = message
        )
        insertLoadingChat(loadingTextChat)

        when (val result = sendTextChatUseCase(message)) {
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

                removeLoadingChat(loadingTextChat)
                NewChatObserver.getInstance().setNewChat(textChat)
            }
            is Resource.Error -> {
                infoLog("텍스트 채팅 전송 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                editLoadingChat(loadingTextChat.copy(sendState = Chat.ChatSendState.Failed))
            }
        }
    }


    private fun collectNewChat() = viewModelScope.launch {
        NewChatObserver
            .getInstance()
            .setNewChat(null)
        NewChatObserver
            .getInstance()
            .newChat
            .collect { newChat ->
                runCatching {
                    insertCompleteChat(newChat ?: return@runCatching)
                    infoLog("new chat: $newChat")
                }.onFailure {
                    infoLog("Fail to collectNewChat(): ${it.localizedMessage}")
                }
            }
    }

    private fun collectPartnerChatRoomState() = viewModelScope.launch {
        PartnerChatRoomStateObserver
            .getInstance()
            .setInChat(false)
        PartnerChatRoomStateObserver
            .getInstance()
            .isInChat
            .collect { isInChat ->
                runCatching {
                    _isPartnerInChat.value = isInChat
                }.onFailure {
                    infoLog("collectPartnerChatRoomState(): ${it.localizedMessage}")
                }
            }
    }

    fun toggleIsPartnerInChat() {
        _isPartnerInChat.value = _isPartnerInChat.value?.not()
        infoLog("isPartnerInChat: ${_isPartnerInChat.value}")
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


    fun sendVoiceChat(context: Context, retryChat: VoiceChat? = null, onSend: () -> Unit = {}) = viewModelScope.launch {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val now = Date()

        val voiceFile = if (retryChat == null) {
            val voiceCacheFile = File(context.cacheDir, Constants.VOICE_CACHE_FILE_NAME)
            if (!voiceCacheFile.exists() || !voiceCacheFile.canRead()) {
                infoLog("보이스 캐시 파일을 읽을 수 없습니다.")
                onSend()
                return@launch
            }
            voiceCacheFile.copyTo(
                target = File(context.cacheDir, "${now.time}.wav"),
                overwrite = true
            )
        } else {
            File(context.cacheDir, "${retryChat.index}.wav")
        }

        val loadingVoiceChat = VoiceChat(
            index = retryChat?.index ?: now.time,
            pageNo = 0,
            chatSender = "me",
            isRead = true,
            createAt = format.format(Date()),
            sendState = Chat.ChatSendState.Sending,
            url = "index",
        )
        insertLoadingChat(loadingVoiceChat)
        onSend()

        when (val result = sendVoiceChatUseCase(voiceFile)) {
            is Resource.Success -> {
                val voiceChat = result.data.run {
                    withContext(Dispatchers.IO) {
                        voiceFile.copyTo(
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
                        sendState = Chat.ChatSendState.Completed,
                        url = "index"
                    )
                }

                removeLoadingChat(loadingVoiceChat)
                NewChatObserver.getInstance().setNewChat(voiceChat)
            }
            is Resource.Error -> {
                infoLog("보이스 채팅 전송 실패: ${result.throwable.stackTrace.joinToString("\n")}")
                editLoadingChat(loadingVoiceChat.copy(sendState = Chat.ChatSendState.Failed))
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
//        _voiceSampler.value?.stopRecording()
        _voiceSampler.value?.release()
        _isVoiceRecordingFinished.value = true
        recordTimer?.cancel()
    }

    fun cancelVoiceRecordingMode() {
        _isVoiceRecordingMode.value = false
        _isVoiceRecordingFinished.value = false
        recordTimer?.cancel()
//        _voiceSampler.value?.stopRecording()
        _voiceSampler.value?.release()
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


    /** Image **/

    fun sendImageChat(context: Context, uri: Uri? = null, retryChat: ImageChat? = null) = viewModelScope.launch {
        if (uri == null) return@launch
        val bitmap = uri.toBitmap(context)
        if (bitmap != null) {
            infoLog("bitmap length: ${bitmap.byteCount / 512} mb")
        }

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val now = Date()

        val loadingImageChat = ImageChat(
            index = now.time,
            pageNo = 0,
            chatSender = "me",
            isRead = true,
            createAt = format.format(Date()),
            sendState = Chat.ChatSendState.Sending,
            url = "index",
            bitmap = bitmap,
            uri = uri
        )
        insertLoadingChat(loadingImageChat)

        // after send chat

        launch {

            delay(1000)
            val next = Date()

            val imageChat = ImageChat(
                index = next.time,
                pageNo = 0,
                chatSender = "me",
                isRead = true,
                createAt = format.format(next),
                sendState = Chat.ChatSendState.Completed,
                url = "index",
                bitmap = bitmap,
                uri = uri
            )

            // 성공했을 때
//            removeLoadingChat(loadingImageChat)
//            NewChatObserver.getInstance().setNewChat(imageChat)

            // 실패했을 때
            editLoadingChat(loadingImageChat.copy(sendState = Chat.ChatSendState.Failed))
        }



    }




}