package com.clonect.feeltalk.new_presentation.ui.main_navigation.question

import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.new_domain.model.question.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(

) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(listOf(
        Question(9, "난 이게 가장 좋더라!", "당신이 가장 좋아하는 스킨십은?", "2023.05.25", null, null),
        Question(8, "이런 순간이 좋아!", "연인이 가장 예뻐 보이는 순간은?", "2023.05.24", "내 답변", "연인 답변"),
        Question(7, "이런 순간이 좋아!", "연인이 가장 예뻐 보이는 순간은?", "2023.05.23", null, "연인 답변"),
        Question(6, "이런 순간이 좋아!", "연인이 가장 예뻐 보이는 순간은?", "2023.05.22", "내 답변", null),
        Question(5, "이런 순간이 좋아!", "연인이 가장 예뻐 보이는 순간은?", "2023.05.21", null, null),
        Question(4, "이런 순간이 좋아!", "연인이 가장 예뻐 보이는 순간은?", "2023.05.20", "내 답변", "연인 답변"),
        Question(3, "이런 순간이 좋아!", "연인이 가장 예뻐 보이는 순간은?", "2023.05.19", null, "연인 답변"),
        Question(2, "이런 순간이 좋아!", "연인이 가장 예뻐 보이는 순간은?", "2023.05.18", "내 답변", null),
        Question(1, "이런 순간이 좋아!", "연인이 가장 예뻐 보이는 순간은?", "2023.05.17", null, null),
    ))
    val questions = _questions.asStateFlow()

    private val _todayQuestion = MutableStateFlow(
        Question(
            index = 9,
            header = "난 이게 가장 좋더라!",
            body = "당신이 가장 좋아하는 스킨십은?",
            date = "2023.05.25",
            myAnswer = null,
            partnerAnswer = null
        )
    )
    val todayQuestion = _todayQuestion.asStateFlow()

    fun setTodayQuestionAnswer(answer: String) {
        if (_todayQuestion.value.myAnswer == null) {
            _todayQuestion.value = _todayQuestion.value.copy(myAnswer = null)
            _todayQuestion.value =  _todayQuestion.value.copy(myAnswer = answer)
        } }
}