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
import com.clonect.feeltalk.new_data.mapper.toChallenge
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.chat.ChatType
import com.clonect.feeltalk.new_domain.model.chat.PartnerLastChatDto
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.usecase.challenge.GetChallengeUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.GetPartnerLastChatUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetQuestionUseCase
import com.clonect.feeltalk.new_presentation.notification.NotificationHelper
import com.clonect.feeltalk.new_presentation.notification.observer.NewChatObserver
import com.clonect.feeltalk.new_presentation.ui.activity.MainActivity
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val getPartnerLastChatUseCase: GetPartnerLastChatUseCase,
    private val getQuestionUseCase: GetQuestionUseCase,
    private val getChallengeUseCase: GetChallengeUseCase,
): ViewModel() {

    private val _navigateTo = MutableSharedFlow<String>()
    val navigateTo = _navigateTo.asSharedFlow()


    private val _partnerLastChat = MutableStateFlow<PartnerLastChatDto?>(null)
    val partnerLastChat = _partnerLastChat.asStateFlow()

    private val _showPartnerLastChat = MutableStateFlow(true)
    val showPartnerLastChat = _showPartnerLastChat.asStateFlow()

    private val isShowQuestionPage = MutableStateFlow(false)

    private val isInQuestionTop = MutableStateFlow(true)


    private val _showChatNavigation = MutableStateFlow(false)
    val showChatNavigation = _showChatNavigation.asStateFlow()


    private val _isUserAnswering = MutableStateFlow(false)
    val isUserAnswering = _isUserAnswering.asStateFlow()

    private val _answerTargetQuestion = MutableStateFlow<Question?>(null)
    val answerTargetQuestion = _answerTargetQuestion.asStateFlow()

    private val _showAnswerSheet = MutableStateFlow(false)
    val showAnswerSheet = _showAnswerSheet.asStateFlow()


    private val _showChallengeDetail = MutableStateFlow<Challenge?>(null)
    val showChallengeDetail = _showChallengeDetail.asStateFlow()


    private val _showInquirySucceedSheet = MutableStateFlow(false)
    val showInquirySucceedSheet = _showInquirySucceedSheet.asStateFlow()

    private val _showSuggestionSucceedSheet = MutableStateFlow(false)
    val showSuggestionSucceedSheet = _showSuggestionSucceedSheet.asStateFlow()


    init {
        getPartnerLastChat()
        collectNewChat()
        calculateShowingPartnerLastChat()
    }

    fun navigateTo(target: String) = viewModelScope.launch {
        if (target == "home" || target == "question" || target == "challenge" || target == "mypage") {
            _navigateTo.emit(target)
        }
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
                _partnerLastChat.value = result.data
                calculateShowingPartnerLastChat()
            }
            is Resource.Error -> {
                infoLog("연인의 가장 최근 채팅 가져오기 실패: ${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun setPartnerLastChat(partnerLastChatDto: PartnerLastChatDto?) {
        _partnerLastChat.value = partnerLastChatDto
        calculateShowingPartnerLastChat()
    }

    fun setShowQuestionPage(isShow: Boolean) {
        isShowQuestionPage.value = isShow
        calculateShowingPartnerLastChat()
    }

    fun setInQuestionTop(isTop: Boolean) {
        isInQuestionTop.value = isTop
        calculateShowingPartnerLastChat()
    }

    private fun calculateShowingPartnerLastChat() {
        _showPartnerLastChat.value = run {
            if (_showChatNavigation.value) return@run false
            if (_showAnswerSheet.value) return@run false
            if (_partnerLastChat.value == null) return@run false
            if (_partnerLastChat.value?.isRead == true) return@run false
            if (!isShowQuestionPage.value) return@run true
            if (!isInQuestionTop.value) return@run false

            return@run true
        }
    }


    fun toggleShowChatNavigation() {
        _showChatNavigation.value = _showChatNavigation.value.not()
        calculateShowingPartnerLastChat()
    }

    fun setShowChatNavigation(showChat: Boolean) {
        _showChatNavigation.value = showChat
        calculateShowingPartnerLastChat()
    }


    fun setUserAnswering(isAnswering: Boolean) {
        _isUserAnswering.value = isAnswering
    }

    fun setAnswerTargetQuestion(question: Question?) {
        _answerTargetQuestion.value = question
    }

    fun setShowAnswerSheet(isShow: Boolean) {
        _showAnswerSheet.value = isShow
        calculateShowingPartnerLastChat()
    }


    fun setShowChallengeDetail(challenge: Challenge?) {
        _showChallengeDetail.value = challenge
    }


    fun setShowInquirySucceedSheet(isShow: Boolean) {
        _showInquirySucceedSheet.value =isShow
    }

    fun setShowSuggestionSucceedSheet(isShow: Boolean) {
        _showSuggestionSucceedSheet.value =isShow
    }



    fun initShowQuestionAnswerSheet(index: Long) = viewModelScope.launch {
        if (index < 0) return@launch

        when (val result = getQuestionUseCase(index)) {
            is Resource.Success -> {
                setAnswerTargetQuestion(result.data)
                setShowAnswerSheet(true)
            }
            is Resource.Error -> {
                infoLog("Fail to get a question at deeplink: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun initShowChallengeDetail(index: Long) = viewModelScope.launch {
        if (index < 0) return@launch

        when (val result = getChallengeUseCase(index)) {
            is Resource.Success -> {
                val challenge = result.data.toChallenge()
                setShowChallengeDetail(challenge)
            }
            is Resource.Error -> {
                infoLog("Fail to get a question at deeplink: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
            }
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
                        ChatType.QuestionChatting -> {
                            "(질문 공유 채팅)"
                        }
                        else -> return@collect
                    }

                    _partnerLastChat.value = PartnerLastChatDto(message, newChat.isRead)
                    calculateShowingPartnerLastChat()

                }.onFailure {
                    infoLog("collectNewChat(): ${it.localizedMessage}")
                }
            }
    }
}