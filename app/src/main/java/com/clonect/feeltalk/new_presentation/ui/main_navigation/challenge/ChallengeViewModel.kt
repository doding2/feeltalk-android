package com.clonect.feeltalk.new_presentation.ui.main_navigation.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(

) : ViewModel() {

    // TODO 임시 코드라 나중에 삭제 요망
    private fun calDeadline(plus: Int): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, plus)
        return cal.time
    }

    private val _challenges = MutableStateFlow<List<Challenge>>(listOf(
        Challenge(1, "운동", "10kg 감량", "못 이뤄도 최대한 해보자",  calDeadline(1), "me", true),
        Challenge(2, "식사", "한 번쯤 비싼 레스토랑 가보자", "근데 그 돈은 햄버거 10개는 먹겠다",  calDeadline(2), "partner", false),
        Challenge(3, "데이트", "부모님 안 계신날 체크체크", null, calDeadline(3), "partner", false),
        Challenge(4, "운동", "자전거 타고 출퇴근 도전!!!", null,  calDeadline(4), "partner", true),
        Challenge(5, "식사", "발렌타인 데이 초콜릿 선물 받고싶어", "이거도 식사 태그 맞나?",  calDeadline(5), "me", false),
        Challenge(6, "식사", "저번주에 생긴 라멘집 가보기", "맛 없으면 ㅈㅅ",  calDeadline(6), "me", true),
        Challenge(7, "데이트", "롯데월드에서 하루종일 놀자", "사람 없는 날 눈치 싸움 잘 해야됨", calDeadline(7), "partner", false),
        Challenge(8, "데이트", "우리집 집들이 파티", "선물 필수",calDeadline(8), "me", true),
        Challenge(9, "여행", "일본 가기", null, calDeadline(9), "partner", true),
        Challenge(10, "여행", "여행 자금 모으기", "200만원이 목표", calDeadline(10), "me", true),
    ))
    val challenges = _challenges.asStateFlow()


    private val _ongoingFragmentScrollToTop = MutableSharedFlow<Boolean>()
    val ongoingFragmentScrollToTop = _ongoingFragmentScrollToTop.asSharedFlow()

    private val _completedFragmentScrollToTop = MutableSharedFlow<Boolean>()
    val completedFragmentScrollToTop = _completedFragmentScrollToTop.asSharedFlow()


    fun setOngoingFragmentScrollToTop() = viewModelScope.launch {
        _ongoingFragmentScrollToTop.emit(true)
    }

    fun setCompletedFragmentScrollToTop() = viewModelScope.launch {
        _completedFragmentScrollToTop.emit(true)
    }
}