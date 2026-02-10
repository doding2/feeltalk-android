package com.clonect.feeltalk.mvp_presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_data.mapper.toStringLowercase
import com.clonect.feeltalk.mvp_domain.model.data.question.Question2
import com.clonect.feeltalk.mvp_domain.model.data.user.Emotion
import com.clonect.feeltalk.mvp_domain.model.data.user.UserInfo
import com.clonect.feeltalk.release_domain.usecase.appSettings.GetAppSettingsUseCase
import com.clonect.feeltalk.mvp_domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.mvp_domain.usecase.question.GetTodayQuestionAnswersFromServer
import com.clonect.feeltalk.mvp_domain.usecase.question.GetTodayQuestionUseCase2
import com.clonect.feeltalk.mvp_domain.usecase.user.*
import com.clonect.feeltalk.release_domain.model.appSettings.AppSettings
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPartnerInfoFlow2UseCase: GetPartnerInfoFlow2UseCase,
    private val updateMyEmotionUseCase: UpdateMyEmotionUseCase,
    private val getTodayQuestionUseCase2: GetTodayQuestionUseCase2,
    private val getTodayQuestionAnswersFromServer: GetTodayQuestionAnswersFromServer,
    private val getCoupleAnniversaryUseCase: GetCoupleAnniversaryUseCase,
    private val requestChangingPartnerEmotionUseCase: RequestChangingPartnerEmotionUseCase,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
): ViewModel() {

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo = _userInfo.asStateFlow()

    private val _partnerInfo = MutableStateFlow(UserInfo())
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _dday = MutableStateFlow<Long?>(null)
    val dday = _dday.asStateFlow()

    private val _todayQuestion2 = MutableStateFlow<Question2?>(null)
    val todayQuestion = _todayQuestion2.asStateFlow()

    private val _partnerClickCount = MutableStateFlow(0)
    val partnerClickCount = _partnerClickCount.asStateFlow()

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings = _appSettings.asStateFlow()

    private val _toast = MutableSharedFlow<String>()
    val toast = _toast.asSharedFlow()


    init {
        getUserInfo()
        getPartnerInfo()
        getTodayQuestion()
        getCoupleAnniversary()
        getAppSettings()
    }


    fun increaseClickCount() {
        _partnerClickCount.value += 1
    }

    fun clearClickCount() {
        _partnerClickCount.value = 0
    }

    fun changeMyEmotion(emotion: Emotion) = viewModelScope.launch(Dispatchers.IO) {
        when (updateMyEmotionUseCase(emotion)) {
            is Resource.Success -> {
                _userInfo.value = _userInfo.value.copy(emotion = emotion)
                infoLog("update my emotion")
                changeEmotionMixpanel(emotion)
            }
            else -> {
                infoLog("Fail to update my emotion")
                sendToast("내 감정 변경에 실패했습니다")
            }
        }
    }

    fun requestChangingPartnerEmotion() = viewModelScope.launch(Dispatchers.IO) {
        val result = requestChangingPartnerEmotionUseCase()
        when (result) {
            is Resource.Success -> {
                sendToast("연인에게 시그널을 보냈어요 !")
                infoLog("Success to request changing partner emotion")
                sendSignalMixpanel()
            }
            is Resource.Error -> {
                sendToast("실패했습니다")
                infoLog("Fail to request changing partner emotion: ${result.throwable.localizedMessage}")
            }
            else -> {
                sendToast("실패했습니다")
                infoLog("Fail to request changing partner emotion")
            }
        }
    }

    fun sendToast(message: String) = viewModelScope.launch {
        _toast.emit(message)
    }


    private fun getUserInfo() = viewModelScope.launch(Dispatchers.IO) {
        val result = getUserInfoUseCase()
        when (result) {
            is Resource.Success -> _userInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get user info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get user info")
        }
    }

    private fun getPartnerInfo() = viewModelScope.launch(Dispatchers.IO) {
        getPartnerInfoFlow2UseCase().collectLatest { result ->
            when (result) {
                is Resource.Success -> _partnerInfo.value = result.data
                is Resource.Error -> infoLog("Fail to get partner info: ${result.throwable.localizedMessage}")
                else -> infoLog("Fail to get partner info")
            }
        }
    }

    private fun getCoupleAnniversary() = viewModelScope.launch(Dispatchers.IO) {
        val result = getCoupleAnniversaryUseCase()
        when (result) {
            is Resource.Success -> {
                val date = result.data
                _dday.value = calculateDDay(date)
            }
            is Resource.Error -> infoLog("Fail to get d day: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner d day")
        }
    }

    private fun getTodayQuestion() = viewModelScope.launch(Dispatchers.IO) {
        val result = getTodayQuestionUseCase2()
        when (result) {
            is Resource.Success -> {
                getTodayQuestionAnswer(result.data)
                infoLog("Today Question: ${_todayQuestion2.value?.question}")
            }
            is Resource.Error -> {
                _todayQuestion2.value = Question2("")
                infoLog("Fail to load today question: ${result.throwable.localizedMessage}")
            }
        }
    }

    private suspend fun getTodayQuestionAnswer(question2: Question2) {
        val result = getTodayQuestionAnswersFromServer()
        when (result) {
            is Resource.Success -> {
                _todayQuestion2.value = question2.copy(
                    myAnswer = result.data.myAnswer,
                    partnerAnswer = result.data.partnerAnswer
                )
            }
            is Resource.Error -> {
                _todayQuestion2.value = question2
                infoLog("Fail to load today question answers: ${result.throwable.localizedMessage}")
            }
        }
    }

    fun getAppSettings() {
        _appSettings.value = getAppSettingsUseCase()
    }


    private fun calculateDDay(date: String): Long {
        try {
            val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val anniversaryDate = format.parse(date) ?: return 0
            val ddayPoint = (Date().time - anniversaryDate.time).toDouble() / Constants.ONE_DAY
            return ceil(ddayPoint).toLong()
        } catch (e: Exception) {
            return 0
        }
    }


    private fun changeEmotionMixpanel(changedEmotion: Emotion) = CoroutineScope(Dispatchers.IO).launch {
        val userInfo = getUserInfoUseCase()
        if (userInfo !is Resource.Success) return@launch

        val mixpanel = getMixpanelAPIUseCase()
        mixpanel.identify(userInfo.data.email, true)
        mixpanel.registerSuperProperties(JSONObject().apply {
            put("gender", userInfo.data.gender)
        })

        val mixpanelDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        mixpanel.track("Change Emotion", JSONObject().apply {
            put("changedEmotion", changedEmotion.toStringLowercase())
            put("changeDate", mixpanelDateFormat.format(Date()))
        })
        mixpanel.people.set("emotion", changedEmotion.toStringLowercase())
    }

    private fun sendSignalMixpanel() = CoroutineScope(Dispatchers.IO).launch {val userInfo = getUserInfoUseCase()
        if (userInfo !is Resource.Success) return@launch

        val mixpanel = getMixpanelAPIUseCase()
        mixpanel.identify(userInfo.data.email, true)
        mixpanel.registerSuperProperties(JSONObject().apply {
            put("gender", userInfo.data.gender)
        })

        val mixpanelDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        mixpanel.track("Send Signal", JSONObject().apply {
            put("sendDate", mixpanelDateFormat.format(Date()))
        })
    }

}