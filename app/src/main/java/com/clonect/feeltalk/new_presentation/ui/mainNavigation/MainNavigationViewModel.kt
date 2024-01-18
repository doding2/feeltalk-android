package com.clonect.feeltalk.new_presentation.ui.mainNavigation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.new_domain.model.appSettings.AppSettings
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.chat.ChatType
import com.clonect.feeltalk.new_domain.model.chat.PartnerLastChatDto
import com.clonect.feeltalk.new_domain.model.chat.TextChat
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.appSettings.SaveAppSettingsUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.GetChallengeUpdatedFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.GetChallengeUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.SetChallengeUpdatedUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.GetNewChatFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.GetPartnerLastChatUseCase
import com.clonect.feeltalk.new_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.new_domain.usecase.mixpanel.OpenSignalSheetMixpanelUseCase
import com.clonect.feeltalk.new_domain.usecase.mixpanel.SetInAnswerSheetMixpanelUseCase
import com.clonect.feeltalk.new_domain.usecase.mixpanel.SetInQuestionPageMixpanelUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetQuestionUpdatedFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetQuestionUseCase
import com.clonect.feeltalk.new_domain.usecase.question.SetQuestionUpdatedUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetPartnerSignalUseCase
import com.clonect.feeltalk.new_presentation.service.notification.NotificationHelper
import com.clonect.feeltalk.new_presentation.ui.activity.MainActivity
import com.clonect.feeltalk.presentation.utils.infoLog
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val saveAppSettingsUseCase: SaveAppSettingsUseCase,
    private val getPartnerLastChatUseCase: GetPartnerLastChatUseCase,
    private val getQuestionUseCase: GetQuestionUseCase,
    private val getChallengeUseCase: GetChallengeUseCase,
    private val getNewChatFlowUseCase: GetNewChatFlowUseCase,
    private val getQuestionUpdatedFlowUseCase: GetQuestionUpdatedFlowUseCase,
    private val setQuestionUpdatedUseCase: SetQuestionUpdatedUseCase,
    private val getChallengeUpdatedFlowUseCase: GetChallengeUpdatedFlowUseCase,
    private val setChallengeUpdatedUseCase: SetChallengeUpdatedUseCase,
    private val getPartnerSignalUseCase: GetPartnerSignalUseCase,
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
    private val setInQuestionPageMixpanelUseCase: SetInQuestionPageMixpanelUseCase,
    private val setInAnswerSheetMixpanelUseCase: SetInAnswerSheetMixpanelUseCase,
    private val openSignalSheetMixpanelUseCase: OpenSignalSheetMixpanelUseCase,
): ViewModel() {

    // For reducing inflating delay due to complex and too much view components
    @SuppressLint("StaticFieldLeak")
    var mainNavView: View? = null

    private val _navigateTo = MutableSharedFlow<String>()
    val navigateTo = _navigateTo.asSharedFlow()


    private val _partnerLastChat = MutableStateFlow<PartnerLastChatDto?>(null)
    val partnerLastChat = _partnerLastChat.asStateFlow()

    private val _lastChatColor = MutableStateFlow(Color.WHITE)
    val lastChatColor = _lastChatColor.asStateFlow()

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


    private val _showSignalSheet = MutableStateFlow(false)
    val showSignalSheet = _showSignalSheet.asStateFlow()


    private val _showChallengeDetail = MutableStateFlow<Challenge?>(null)
    val showChallengeDetail = _showChallengeDetail.asStateFlow()


    private val _showInquirySucceedSheet = MutableStateFlow(false)
    val showInquirySucceedSheet = _showInquirySucceedSheet.asStateFlow()

    private val _showSuggestionSucceedSheet = MutableStateFlow(false)
    val showSuggestionSucceedSheet = _showSuggestionSucceedSheet.asStateFlow()

    private val _showSignalCompleteSheet = MutableStateFlow(false)
    val showSignalCompleteSheet = _showSignalCompleteSheet.asStateFlow()


    private val _isQuestionUpdated = MutableStateFlow(false)
    val isQuestionUpdated = _isQuestionUpdated.asStateFlow()

    private val _isChallengeUpdated = MutableStateFlow(false)
    val isChallengeUpdated = _isChallengeUpdated.asStateFlow()



    init {
        getPartnerLastChat()
        collectNewChat()
        collectQuestionUpdated()
        collectChallengeUpdated()
        calculateShowingPartnerLastChat()
    }

    fun navigateTo(target: String) = viewModelScope.launch {
        if (target == "home" || target == "question" || target == "challenge" || target == "mypage") {
            _navigateTo.emit(target)
        }
    }

    fun enablePushNotificationEnabled(enabled: Boolean) {
        FirebaseMessaging.getInstance().apply {
            token.addOnSuccessListener {
                val appSettings = getAppSettingsUseCase()
                appSettings.fcmToken = it
                appSettings.isPushNotificationEnabled = enabled
                saveAppSettings(appSettings)
            }
        }
    }

    private fun saveAppSettings(appSettings: AppSettings) = viewModelScope.launch(Dispatchers.IO) {
        saveAppSettingsUseCase(appSettings)
    }


    fun setShortcut(context: Context) = viewModelScope.launch {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return@launch

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("showChat", true)
        }

//        val partnerSignal = (getPartnerSignalUseCase() as? Resource.Success)?.data ?: Signal.Half
//        val partnerSignalRes = when (partnerSignal) {
//            Signal.Zero -> R.drawable.n_image_signal_0
//            Signal.Quarter -> R.drawable.n_image_signal_25
//            Signal.Half -> R.drawable.n_image_signal_50
//            Signal.ThreeFourth -> R.drawable.n_image_signal_75
//            Signal.One -> R.drawable.n_image_signal_100
//        }

        val partner = android.app.Person.Builder()
            .setName(context.getString(R.string.notification_partner))
//            .setIcon(IconCompat.createWithResource(context, partnerSignalRes).toIcon(context))
            .build()

        val shortcut = ShortcutInfo.Builder(context, NotificationHelper.CHAT_SHORTCUT_ID).run {
            setLongLived(true)
            setShortLabel(partner.name!!)
            setPerson(partner)
            setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher_round).toIcon(context))
            setCategories(setOf(ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION))
            setIntent(intent)
            build()
        }

        val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
        shortcutManager.pushDynamicShortcut(shortcut)
    }

    private fun getPartnerLastChat() = viewModelScope.launch(Dispatchers.IO) {
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

    fun setLastChatColor(color: Int) {
        _lastChatColor.value = color
    }

    fun setPartnerLastChat(partnerLastChatDto: PartnerLastChatDto?) {
        _partnerLastChat.value = partnerLastChatDto
        calculateShowingPartnerLastChat()
    }

    fun getShowQuestionPage() = isShowQuestionPage.value

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
            if (_showSignalSheet.value) return@run false
            if (_showAnswerSheet.value) return@run false
            if (_showInquirySucceedSheet.value) return@run false
            if (_showSuggestionSucceedSheet.value) return@run false
            if (_showSignalCompleteSheet.value) return@run false
            if (_partnerLastChat.value == null) return@run false
            if (_partnerLastChat.value?.isRead == true) return@run false
            if (isShowQuestionPage.value && !isInQuestionTop.value) return@run false

            return@run true
        }
    }



    fun toggleShowChatNavigation() {
        _showChatNavigation.value = _showChatNavigation.value.not()
        if (_showChatNavigation.value) {
            setPartnerLastChat(null)
        }
        calculateShowingPartnerLastChat()
    }

    fun setShowChatNavigation(showChat: Boolean) {
        _showChatNavigation.value = showChat
        if (showChat) {
            setPartnerLastChat(null)
        }
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


    fun setShowSignalSheet(isShow: Boolean) {
        _showSignalSheet.value = isShow
        calculateShowingPartnerLastChat()
    }


    fun setShowChallengeDetail(challenge: Challenge?) {
        _showChallengeDetail.value = challenge
    }


    fun setShowInquirySucceedSheet(isShow: Boolean) {
        _showInquirySucceedSheet.value = isShow
    }

    fun setShowSuggestionSucceedSheet(isShow: Boolean) {
        _showSuggestionSucceedSheet.value = isShow
    }

    fun setShowSignalCompleteSheet(isShow: Boolean) {
        _showSignalCompleteSheet.value = isShow
    }



    fun initShowQuestionAnswerSheet(index: Long) = viewModelScope.launch(Dispatchers.IO) {
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

    fun initShowChallengeDetail(index: Long) = viewModelScope.launch(Dispatchers.IO) {
        if (index < 0) return@launch

        when (val result = getChallengeUseCase(index)) {
            is Resource.Success -> {
                val challenge = result.data
                setShowChallengeDetail(challenge)
            }
            is Resource.Error -> {
                infoLog("Fail to get a question at deeplink: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }


    private fun collectNewChat() = viewModelScope.launch {
        getNewChatFlowUseCase().collect {
            if (it.chatSender == "me") return@collect

            val message  = when (it.type) {
                ChatType.TextChatting -> {
                    val textChat = it as TextChat
                    textChat.message
                }
                ChatType.VoiceChatting -> {
                    "(보이스 채팅)"
                }
                ChatType.QuestionChatting -> {
                    "(질문 공유 채팅)"
                }
                else -> "(${it.type.raw} 채팅)"
            }

            _partnerLastChat.value = PartnerLastChatDto(message, it.isRead)
            calculateShowingPartnerLastChat()
        }
    }

    private fun collectQuestionUpdated() = viewModelScope.launch {
        getQuestionUpdatedFlowUseCase().collectLatest {
            _isQuestionUpdated.value = it
        }
    }

    fun setQuestionUpdated(isUpdated: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        setQuestionUpdatedUseCase(isUpdated)
            .onSuccess { _isQuestionUpdated.value = isUpdated }
            .onError { infoLog("Fail to set question updated") }
    }


    private fun collectChallengeUpdated() = viewModelScope.launch {
        getChallengeUpdatedFlowUseCase().collectLatest {
            _isChallengeUpdated.value = it
        }
    }

    fun setChallengeUpdated(isUpdated: Boolean) = viewModelScope.launch {
        setChallengeUpdatedUseCase(isUpdated)
            .onSuccess { _isQuestionUpdated.value = isUpdated }
            .onError { infoLog("Fail to set challenge updated") }
    }


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }

    fun setInQuestionPage(isInQuestion: Boolean) = viewModelScope.launch {
        setInQuestionPageMixpanelUseCase(isInQuestion)
    }

    fun setInAnswerSheet(isInAnswer: Boolean) = viewModelScope.launch {
        setInAnswerSheetMixpanelUseCase(isInAnswer)
    }

    fun openSignalSheet() = viewModelScope.launch {
        openSignalSheetMixpanelUseCase()
    }
}