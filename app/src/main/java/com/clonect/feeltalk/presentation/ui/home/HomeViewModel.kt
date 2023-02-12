package com.clonect.feeltalk.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.data.user.Emotion
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.usecase.encryption.TestUseCase
import com.clonect.feeltalk.domain.usecase.question.GetTodayQuestionAnswersFromServer
import com.clonect.feeltalk.domain.usecase.question.GetTodayQuestionUseCase
import com.clonect.feeltalk.domain.usecase.user.GetCoupleAnniversaryUseCase
import com.clonect.feeltalk.domain.usecase.user.GetPartnerInfoUseCase
import com.clonect.feeltalk.domain.usecase.user.GetUserInfoUseCase
import com.clonect.feeltalk.domain.usecase.user.UpdateMyEmotionUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getPartnerInfoUseCase: GetPartnerInfoUseCase,
    private val updateMyEmotionUseCase: UpdateMyEmotionUseCase,
    private val getTodayQuestionUseCase: GetTodayQuestionUseCase,
    private val getTodayQuestionAnswersFromServer: GetTodayQuestionAnswersFromServer,
    private val getCoupleAnniversaryUseCase: GetCoupleAnniversaryUseCase,
    private val testUseCase: TestUseCase
): ViewModel() {

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo = _userInfo.asStateFlow()

    private val _partnerInfo = MutableStateFlow(UserInfo())
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _dday = MutableStateFlow<Long?>(null)
    val dday = _dday.asStateFlow()

    private val _todayQuestion = MutableStateFlow<Question?>(null)
    val todayQuestion = _todayQuestion.asStateFlow()


    init {
        viewModelScope.launch {
//            testUseCase()
        }
        getUserInfo()
        getPartnerInfo()
        getTodayQuestion()
        getCoupleAnniversary()
    }

    fun changeMyEmotion(emotion: Emotion) = viewModelScope.launch(Dispatchers.IO) {
        when (updateMyEmotionUseCase(emotion)) {
            is Resource.Success -> {
                _userInfo.value = _userInfo.value.copy(emotion = emotion)
                infoLog("update")
            }
            else -> infoLog("Fail to update my emotion")
        }
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
        val result = getPartnerInfoUseCase()
        when (result) {
            is Resource.Success -> _partnerInfo.value = result.data
            is Resource.Error -> infoLog("Fail to get partner info: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner info")
        }
    }

    private fun getCoupleAnniversary() = viewModelScope.launch(Dispatchers.IO) {
        val result = getCoupleAnniversaryUseCase()
        when (result) {
            is Resource.Success -> {
                val date = result.data
                infoLog("couple anniversary date: ${date}")
                _dday.value = calculateDDay(date)
            }
            is Resource.Error -> infoLog("Fail to get d day: ${result.throwable.localizedMessage}")
            else -> infoLog("Fail to get partner d day")
        }
    }

    private fun getTodayQuestion() = viewModelScope.launch(Dispatchers.IO) {
        val result = getTodayQuestionUseCase()
        when (result) {
            is Resource.Success -> {
                _todayQuestion.value = result.data
                getTodayQuestionAnswer()
                infoLog("Today Question: ${_todayQuestion.value}")
            }
            is Resource.Error -> infoLog("Fail to load today question: ${result.throwable.localizedMessage}")
            is Resource.Loading -> infoLog("Fail to load today question")
        }
    }

    private suspend fun getTodayQuestionAnswer() {
        val result = getTodayQuestionAnswersFromServer()
        when (result) {
            is Resource.Success -> {
                _todayQuestion.value = _todayQuestion.value?.copy(
                    myAnswer = result.data.myAnswer,
                    partnerAnswer = result.data.partnerAnswer
                )
            }
            is Resource.Error -> {
                _todayQuestion.value = Question("")
                infoLog("Fail to load today question answers: ${result.throwable.localizedMessage}")
            }
            is Resource.Loading -> {
                _todayQuestion.value = Question("")
                infoLog("Fail to load today question answers")
            }
        }
    }


    private fun calculateDDay(date: String): Long {
        try {
            val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val anniversaryDate = format.parse(date) ?: return 0
            val anniversaryCalendar = Calendar.getInstance(Locale.getDefault()).apply {
                time = anniversaryDate
            }

            val anniversaryDay = anniversaryCalendar.timeInMillis / Constants.ONE_DAY
            val nowDay = Calendar.getInstance(Locale.getDefault()).timeInMillis / Constants.ONE_DAY

            return nowDay - anniversaryDay
        } catch (e: Exception) {
            return 0
        }
    }
}