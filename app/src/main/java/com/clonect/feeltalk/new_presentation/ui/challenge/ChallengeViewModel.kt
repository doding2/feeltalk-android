package com.clonect.feeltalk.new_presentation.ui.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.plusDayBy
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

    private val _challenges = MutableStateFlow<List<Challenge>>(listOf(
        Challenge("운동", "10kg 감량", "못 이뤄도 최대한 해보자",  Date().plusDayBy(1), "me", true),
        Challenge("식사", "한 번쯤 비싼 레스토랑 가보자", "근데 그 돈은 햄버거 10개는 먹겠다",  Date().plusDayBy(2), "partner", false),
        Challenge("데이트", "부모님 안 계신날 체크체크", null, Date().plusDayBy(3), "partner", false),
        Challenge("운동", "자전거 타고 출퇴근 도전!!!", null,  Date().plusDayBy(4), "partner", true),
        Challenge("식사", "발렌타인 데이 초콜릿 선물 받고싶어", "이거도 식사 태그 맞나?",  Date().plusDayBy(5), "me", false),
        Challenge("식사", "저번주에 생긴 라멘집 가보기", "맛 없으면 ㅈㅅ",  Date().plusDayBy(6), "me", true),
        Challenge("데이트", "롯데월드에서 하루종일 놀자", "사람 없는 날 눈치 싸움 잘 해야됨", Date().plusDayBy(7), "partner", false),
        Challenge("데이트", "우리집 집들이 파티", "선물 필수",Date().plusDayBy(8), "me", true),
        Challenge("여행", "일본 가기", null, Date().plusDayBy(9), "partner", true),
        Challenge("여행", "여행 자금 모으기", "200만원이 목표", Date().plusDayBy(10), "me", true),
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