package com.clonect.feeltalk.presentation.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.chat.Chat2
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.model.dto.question.QuestionDetailDto
import com.clonect.feeltalk.domain.usecase.chat.GetChatListUseCase2
import com.clonect.feeltalk.domain.usecase.chat.ReloadChatListUseCase
import com.clonect.feeltalk.domain.usecase.chat.SendChatUseCase
import com.clonect.feeltalk.domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.domain.usecase.question.GetQuestionDetailUseCase
import com.clonect.feeltalk.domain.usecase.user.*
import com.clonect.feeltalk.new_presentation.service.notification_observer.NewChatObserver
import com.clonect.feeltalk.new_presentation.service.notification_observer.QuestionAnswerObserver
import com.clonect.feeltalk.presentation.ui.FeeltalkApp
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getQuestionDetailUseCase: GetQuestionDetailUseCase,
    private val getPartnerInfoUseCase: GetPartnerInfoUseCase,
    private val getChatListUseCase2: GetChatListUseCase2,
    private val sendChatUseCase: SendChatUseCase,
    private val reloadChatListUseCase: ReloadChatListUseCase,
    private val getMyProfileImageUrlUseCase: GetMyProfileImageUrlUseCase,
    private val getPartnerProfileImageUrlUseCase: GetPartnerProfileImageUrlUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
    private val getUserIsActiveUseCase: GetUserIsActiveUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _partnerInfo = MutableStateFlow(UserInfo())
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _question = MutableStateFlow(Question(""))
    val question = _question.asStateFlow()

    private val _questionDetail = MutableStateFlow<QuestionDetailDto?>(null)
    val questionDetail = _questionDetail.asStateFlow()

    private val _chat2List = MutableStateFlow<List<Chat2>>(emptyList())
    val chatList = _chat2List.asStateFlow()

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
        getQuestionDetail()
        getPartnerInfo()
        getMyProfileImageUrl()
        getPartnerProfileImageUrl()
        collectChatList()
        collectFcmNewChat()
        collectIsAnswerUpdated()
    }


    private fun getQuestionDetail() = viewModelScope.launch(Dispatchers.IO) {
        val result = getQuestionDetailUseCase(_question.value.question)
        when (result) {
            is Resource.Success -> {
                _questionDetail.value = result.data
                infoLog("header: ${result.data.header}, body: ${result.data.body}")
            }
            is Resource.Error -> {
                infoLog("Fail to get question detail: ${_question.value.question}, error: ${result.throwable.localizedMessage}")
                _questionDetail.value = null
            }
            else -> {
                infoLog("Fail to get question detail")
                _questionDetail.value = null
            }
        }
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
//        NewChatObserver.getInstance().newChat.collect {
//            if (it is Resource.Success) {
//                val newList = mutableListOf<Chat2>().apply {
//                    addAll(_chat2List.value)
//                    add(it.data)
//                }
//                _chat2List.value = newList
//            }
//        }
    }

    private fun collectChatList() = viewModelScope.launch(Dispatchers.IO) {
        val questionContent = _question.value.question
        getChatListUseCase2(questionContent)
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

    private fun updateChatList(chat2List: List<Chat2>) {
        val newList = mutableListOf<Chat2>()
        newList.addAll(chat2List)
        newList.sortBy { it.id }

        _isPartnerAnswered.value = newList.any { it.owner == "partner" }
        if (!isPartnerAnswered.value) {
            val waitingChat2 = Chat2(
                id = -1,
                question = _question.value.question,
                owner = "partner",
                message = "",
                date = "",
                isAnswer = true
            )
            newList.add(waitingChat2)
        }

        _chat2List.value = newList
    }


    fun sendChat(content: String, onCompleted: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = format.format(Date())
        infoLog("날짜: ${date}")

        val chat2 = Chat2(
            question = _question.value.question,
            owner = "mine",
            message = content,
            date = date
        )

        val result = sendChatUseCase(chat2)
        when (result) {
            is Resource.Success -> {
                infoLog("Success to send chat: ${result.data}")
                sendChatMixpanel()
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



    private fun sendChatMixpanel() = CoroutineScope(Dispatchers.IO).launch {
        val userInfo = getUserInfoUseCase()
        if (userInfo !is Resource.Success) return@launch

        val mixpanel = getMixpanelAPIUseCase()
        mixpanel.identify(userInfo.data.email, true)
        mixpanel.registerSuperProperties(JSONObject().apply {
            put("gender", userInfo.data.gender)
        })

        val feeltalkDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val mixpanelDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val questionFeeltalkDate = _question.value.questionDate?.let { feeltalkDateFormat.parse(it) }
        val questionMixpanelDate = questionFeeltalkDate?.let { mixpanelDateFormat.format(it) }

        mixpanel.track("Send Chat", JSONObject().apply {
            put("isActive", getUserIsActiveUseCase())
            put("questionDate", questionMixpanelDate)
            put("chatDate", mixpanelDateFormat.format(Date()))
        })
    }


    override fun onCleared() {
        super.onCleared()
        FeeltalkApp.setQuestionIdOfShowingChatFragment(null)
        NewChatObserver.onCleared()
        QuestionAnswerObserver.onCleared()
    }
}