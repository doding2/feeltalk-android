package com.clonect.feeltalk.mvp_presentation.ui.today_question

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.question.Question2
import com.clonect.feeltalk.mvp_domain.model.data.user.UserInfo
import com.clonect.feeltalk.mvp_domain.model.dto.question.QuestionDetailDto
import com.clonect.feeltalk.mvp_domain.usecase.mixpanel.GetMixpanelAPIUseCase
import com.clonect.feeltalk.mvp_domain.usecase.question.GetQuestionAnswersUseCase
import com.clonect.feeltalk.mvp_domain.usecase.question.GetQuestionDetailUseCase
import com.clonect.feeltalk.mvp_domain.usecase.question.SaveQuestionToDatabaseUseCase
import com.clonect.feeltalk.mvp_domain.usecase.question.SendQuestionAnswerUseCase
import com.clonect.feeltalk.mvp_domain.usecase.user.GetPartnerInfo2UseCase
import com.clonect.feeltalk.mvp_domain.usecase.user.GetUserInfoUseCase
import com.clonect.feeltalk.mvp_domain.usecase.user.SetUserIsActiveUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodayQuestionViewModel @Inject constructor(
    private val getQuestionDetailUseCase: GetQuestionDetailUseCase,
    private val sendQuestionAnswerUseCase: SendQuestionAnswerUseCase,
    private val getQuestionAnswersUseCase: GetQuestionAnswersUseCase,
    private val getPartnerInfo2UseCase: GetPartnerInfo2UseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getMixpanelAPIUseCase: GetMixpanelAPIUseCase,
    private val saveQuestionToDatabaseUseCase: SaveQuestionToDatabaseUseCase,
    private val setUserIsActiveUseCase: SetUserIsActiveUseCase,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val homeQuestionReference2 = MutableStateFlow(Question2(""))

    private val _question2StateFlow = MutableStateFlow(Question2(""))
    val questionStateFlow = _question2StateFlow.asStateFlow()

    private val _questionDetail = MutableStateFlow<QuestionDetailDto?>(null)
    val questionDetail = _questionDetail.asStateFlow()

    private val _myAnswerStateFlow = MutableStateFlow("")
    val myAnswerStateFlow: StateFlow<String> = _myAnswerStateFlow.asStateFlow()

    private val _partnerAnswer = MutableStateFlow<String?>(null)
    val partnerAnswer = _partnerAnswer.asStateFlow()

    private val _partnerInfo = MutableStateFlow<UserInfo?>(null)
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        savedStateHandle.get<Question2>("selectedQuestion")?.let {
            setQuestion(it.copy())
            homeQuestionReference2.value = it
        }
        getQuestionDetail()
        getQuestionAnswers()
        getPartnerInfo()
        openQuestionFirsTimeMixpanel()
    }

    private fun getQuestionDetail() = viewModelScope.launch(Dispatchers.IO) {
        val result = getQuestionDetailUseCase(_question2StateFlow.value.question)
        when (result) {
            is Resource.Success -> {
                _questionDetail.value = result.data
                infoLog("header: ${result.data.header}, body: ${result.data.body}")
            }
            is Resource.Error -> {
                infoLog("Fail to get question detail: ${_question2StateFlow.value.question}, error: ${result.throwable.localizedMessage}")
                _questionDetail.value = null
            }
            else -> {
                infoLog("Fail to get question detail")
                _questionDetail.value = null
            }
        }
    }

    private fun getQuestionAnswers() = viewModelScope.launch(Dispatchers.IO) {
        val result = getQuestionAnswersUseCase(_question2StateFlow.value.question)
        when (result) {
            is Resource.Success -> {
                _partnerAnswer.value = result.data.partner ?: ""
            }
            is Resource.Error -> {
                infoLog("Fail to get question answers: ${_question2StateFlow.value.question}, error: ${result.throwable.localizedMessage}")
                _partnerAnswer.value = null
            }
            else -> {
                infoLog("Fail to get question answers: ${_question2StateFlow.value.question}")
                _partnerAnswer.value = null
            }
        }
    }

    private fun getPartnerInfo() = viewModelScope.launch(Dispatchers.IO) {
        val result = getPartnerInfo2UseCase()
        when (result) {
            is Resource.Success -> {
                _partnerInfo.value = result.data
            }
            is Resource.Error -> {
                infoLog("Fail to get partner info: ${result.throwable.localizedMessage}")
            }
            else -> {
                infoLog("Fail to get partner info")
            }
        }
    }


    private fun setQuestion(question2: Question2) {
        _question2StateFlow.value = question2
    }

    fun setMyAnswer(answer: String) {
        _myAnswerStateFlow.value = answer
        _question2StateFlow.value.myAnswer = answer
    }

    suspend fun sendQuestionAnswer() = withContext(Dispatchers.IO) {
        _isLoading.value = true
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = format.format(Date())
        val question = _question2StateFlow.value.apply {
            myAnswerDate = date
        }
        val result = sendQuestionAnswerUseCase(question)
        _isLoading.value = false
        return@withContext when (result) {
            is Resource.Success -> {
                homeQuestionReference2.value.apply {
                    myAnswer = question.myAnswer
                    myAnswerDate = question.myAnswerDate
                }
                answerQuestionMixpanel()
                true
            }
            is Resource.Error -> {
                infoLog("Fail to send question answer: ${result.throwable.localizedMessage}")
                false
            }
            else -> false
        }
    }




    private fun openQuestionFirsTimeMixpanel() = CoroutineScope(Dispatchers.IO).launch {
        if (!_question2StateFlow.value.isFirstOpen) return@launch

        val userInfo = getUserInfoUseCase()
        if (userInfo !is Resource.Success) return@launch

        val mixpanel = getMixpanelAPIUseCase()
        mixpanel.identify(userInfo.data.email, true)
        mixpanel.registerSuperProperties(JSONObject().apply {
            put("gender", userInfo.data.gender)
        })

        val feeltalkDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val mixpanelDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val questionFeeltalkDate = _question2StateFlow.value.questionDate?.let { feeltalkDateFormat.parse(it) }
        val questionMixpanelDate = questionFeeltalkDate?.let { mixpanelDateFormat.format(it) }

        mixpanel.track("Open Question First Time", JSONObject().apply {
            put("openDate", mixpanelDateFormat.format(Date()))
            put("questionDate", questionMixpanelDate)
        })

        _question2StateFlow.value.isFirstOpen = false
        saveQuestionToDatabaseUseCase(_question2StateFlow.value)
    }

    private fun answerQuestionMixpanel() = CoroutineScope(Dispatchers.IO).launch {
        val userInfo = getUserInfoUseCase()
        if (userInfo !is Resource.Success) return@launch

        val mixpanel = getMixpanelAPIUseCase()
        mixpanel.identify(userInfo.data.email, true)
        mixpanel.registerSuperProperties(JSONObject().apply {
            put("gender", userInfo.data.gender)
        })

        val feeltalkDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val mixpanelDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val questionFeeltalkDate = _question2StateFlow.value.questionDate?.let { feeltalkDateFormat.parse(it) }
        val questionMixpanelDate = questionFeeltalkDate?.let { mixpanelDateFormat.format(it) }

        mixpanel.track("Answer Question", JSONObject().apply {
            put("answerDate", mixpanelDateFormat.format(Date()))
            put("questionDate", questionMixpanelDate)
            put("isActive", true)
        })


        setUserIsActiveUseCase()
    }

}