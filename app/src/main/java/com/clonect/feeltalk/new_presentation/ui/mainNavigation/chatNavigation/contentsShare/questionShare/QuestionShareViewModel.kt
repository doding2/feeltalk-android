package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.contentsShare.questionShare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.clonect.feeltalk.common.PageEvents
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.usecase.question.GetAnswerQuestionFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetPagingQuestionUseCase
import com.clonect.feeltalk.new_domain.usecase.question.GetTodayQuestionFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionShareViewModel @Inject constructor(
    getPagingQuestionUseCase: GetPagingQuestionUseCase,
    private val getAnswerQuestionFlowUseCase: GetAnswerQuestionFlowUseCase,
    private val getTodayQuestionFlowUseCase: GetTodayQuestionFlowUseCase,
) : ViewModel() {

    private val isInTop = MutableStateFlow(true)

    private val _scrollToTop = MutableStateFlow(false)
    val scrollToTop = _scrollToTop.asStateFlow()

    init {
        collectTodayQuestion()
        collectQuestionAnswer()
    }

    fun setInTop(isTop: Boolean) {
        isInTop.value = isTop
    }

    fun setScrollToTop(scrollTop: Boolean) = viewModelScope.launch {
        _scrollToTop.value = scrollTop
    }


    /** Pagination **/
    private val pageModificationEvents = MutableStateFlow<List<PageEvents<Question>>>(emptyList())

    private fun applyPageModification(paging: PagingData<Question>, event: PageEvents<Question>): PagingData<Question> {
        return when (event) {
            is PageEvents.Edit -> {
                paging.map {
                    return@map if (it.index == event.item.index)
                        it.copy(
                            myAnswer = it.myAnswer ?: event.item.myAnswer,
                            partnerAnswer = it.partnerAnswer ?: event.item.partnerAnswer
                        )
                    else
                        it
                }
            }
            is PageEvents.Remove -> {
                paging.filter { it.index != event.item.index }
            }
            is PageEvents.InsertItemFooter -> {
                paging.insertFooterItem(item = event.item)
            }
            is PageEvents.InsertItemHeader -> {
                paging.insertHeaderItem(item = event.item)
            }
        }
    }

    fun modifyPage(event: PageEvents<Question>) {
        if (event !in pageModificationEvents.value) {
            pageModificationEvents.value += event
        }
    }


    val pagingQuestion: Flow<PagingData<Question>> = getPagingQuestionUseCase()
        .cachedIn(viewModelScope)
        .combine(pageModificationEvents) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyPageModification(acc, event)
            }
        }


    private fun collectTodayQuestion() = viewModelScope.launch {
        getTodayQuestionFlowUseCase().collect {
            if (it == null) return@collect
            modifyPage(PageEvents.InsertItemHeader(it))

            if (isInTop.value) {
                setScrollToTop(true)
            }
        }
    }

    private fun collectQuestionAnswer() = viewModelScope.launch {
        getAnswerQuestionFlowUseCase().collect {
            modifyPage(PageEvents.Edit(it))
        }
    }


}