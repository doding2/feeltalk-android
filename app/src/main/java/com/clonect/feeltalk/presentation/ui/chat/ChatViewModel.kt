package com.clonect.feeltalk.presentation.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.usecase.chat.GetChatListUseCase
import com.clonect.feeltalk.domain.usecase.chat.ReloadChatListUseCase
import com.clonect.feeltalk.domain.usecase.chat.SendChatUseCase
import com.clonect.feeltalk.domain.usecase.user.GetMyProfileImageUrlUseCase
import com.clonect.feeltalk.domain.usecase.user.GetPartnerInfoUseCase
import com.clonect.feeltalk.domain.usecase.user.GetPartnerProfileImageUrlUseCase
import com.clonect.feeltalk.presentation.service.notification_observer.FcmNewChatObserver
import com.clonect.feeltalk.presentation.service.notification_observer.QuestionAnswerObserver
import com.clonect.feeltalk.presentation.ui.FeeltalkApp
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getPartnerInfoUseCase: GetPartnerInfoUseCase,
    private val getChatListUseCase: GetChatListUseCase,
    private val sendChatUseCase: SendChatUseCase,
    private val reloadChatListUseCase: ReloadChatListUseCase,
    private val getMyProfileImageUrlUseCase: GetMyProfileImageUrlUseCase,
    private val getPartnerProfileImageUrlUseCase: GetPartnerProfileImageUrlUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _partnerInfo = MutableStateFlow(UserInfo())
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _question = MutableStateFlow(Question(""))
    val question = _question.asStateFlow()

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList = _chatList.asStateFlow()

    private val _myProfileImageUrl = MutableStateFlow<String?>(null)
    val myProfileImageUrl = _myProfileImageUrl.asStateFlow()

    private val _partnerProfileImageUrl = MutableStateFlow<String?>(null)
    val partnerProfileImageUrl = _partnerProfileImageUrl.asStateFlow()

    private val _isPartnerAnswered = MutableStateFlow(false)
    val isPartnerAnswered = _isPartnerAnswered.asStateFlow()


    private val _isScrollBottom = MutableStateFlow(true)
    val isScrollBottom = _isScrollBottom.asStateFlow()

    private val _scrollPositionState = MutableStateFlow<Int?>(null)
    val scrollPositionState = _scrollPositionState.asStateFlow()


    init {
        savedStateHandle.get<Question>("selectedQuestion")?.let {
            _question.value = it
            FeeltalkApp.setQuestionIdOfShowingChatFragment(it.question)
            infoLog("Chat Room Entered: $it")
        }
        getPartnerInfo()
        getMyProfileImageUrl()
        getPartnerProfileImageUrl()
        collectChatList()
        collectFcmNewChat()
        collectIsAnswerUpdated()
    }

    private fun collectIsAnswerUpdated() = viewModelScope.launch(Dispatchers.IO) {
        QuestionAnswerObserver
            .getInstance()
            .isAnswerUpdated
            .collectLatest { isUpdated ->
                if (isUpdated) {
                    reloadChatListUseCase(_question.value.question)
                }
            }
    }

    private fun collectFcmNewChat() = viewModelScope.launch(Dispatchers.IO) {
        FcmNewChatObserver.getInstance().newChat.collect {
            if (it is Resource.Success) {
                val newList = mutableListOf<Chat>().apply {
                    addAll(_chatList.value)
                    add(it.data)
                }
                _chatList.value = newList
            }
        }
    }

    private fun collectChatList() = viewModelScope.launch(Dispatchers.IO) {
        val questionContent = _question.value.question
        getChatListUseCase(questionContent)
            .catch { infoLog("collect chat list error: ${it.localizedMessage}") }
            .collectLatest { result ->
            when (result) {
                is Resource.Success -> {
                    updateChatList(result.data)
                    infoLog("Success to get chat list: ${result.data}")
                }
                is Resource.Error -> {
                    infoLog("Fail to get chat list: ${result.throwable.localizedMessage}")
                }
                is Resource.Loading -> {
                    infoLog("Fail to get chat list")
                }
            }
        }
    }

    private fun getMyProfileImageUrl() = viewModelScope.launch(Dispatchers.IO) {
        val result = getMyProfileImageUrlUseCase()
        when (result) {
            is Resource.Success -> { _myProfileImageUrl.value = result.data }
            is Resource.Error -> infoLog("Fail to get my profile image url: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get my profile image url")
        }
    }

    private fun getPartnerProfileImageUrl() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPartnerProfileImageUrlUseCase()
        when (result) {
            is Resource.Success -> { _partnerProfileImageUrl.value = result.data }
            is Resource.Error -> infoLog("Fail to get partner profile image url: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner profile image url")
        }
    }

    private fun getPartnerInfo() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPartnerInfoUseCase()
        when (result) {
            is Resource.Success -> _partnerInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get partner info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner info")
        }
    }

    private fun updateChatList(chatList: List<Chat>) {
        val newList = mutableListOf<Chat>()
        newList.addAll(chatList)
        newList.sortBy { it.id }

        _isPartnerAnswered.value = newList.any { it.owner == "partner" }
        if (!isPartnerAnswered.value) {
            val waitingChat = Chat(
                id = -1,
                question = _question.value.question,
                owner = "partner",
                message = "",
                date = "",
                isAnswer = true
            )
            newList.add(waitingChat)
        }

        _chatList.value = newList
    }


    fun sendChat(content: String, onCompleted: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = format.format(Date())
        infoLog("날짜: ${date}")

        val chat = Chat(
            question = _question.value.question,
            owner = "mine",
            message = content,
            date = date
        )

        val result = sendChatUseCase(chat)
        when (result) {
            is Resource.Success -> {
                infoLog("Success to send chat: ${result.data}")
                setScrollBottom(true)
                onCompleted()
            }
            is Resource.Error -> {
                infoLog("Fail to send chat: ${result.throwable.localizedMessage}")
            }
            else -> {
                infoLog("Fail to send chat")
            }
        }
    }


    fun updateScrollPosition(position: Int?) {
        _scrollPositionState.value = position
    }

    fun setScrollBottom(isBottom: Boolean) {
        _isScrollBottom.value = isBottom
    }


    override fun onCleared() {
        super.onCleared()
        FeeltalkApp.setQuestionIdOfShowingChatFragment(null)
        FcmNewChatObserver.onCleared()
        QuestionAnswerObserver.onCleared()
    }
}