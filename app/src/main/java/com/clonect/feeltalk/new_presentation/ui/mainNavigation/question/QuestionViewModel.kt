package com.clonect.feeltalk.new_presentation.ui.mainNavigation.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.clonect.feeltalk.new_domain.model.page.PageEvents
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.usecase.question.GetPagingQuestionUseCase
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.QuestionAnswerObserver
import com.clonect.feeltalk.new_presentation.notification.notificationObserver.TodayQuestionObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val getPagingQuestionUseCase: GetPagingQuestionUseCase,
) : ViewModel() {

    private val isInQuestionTop = MutableStateFlow(true)

    private val _scrollToTop = MutableSharedFlow<Boolean>()
    val scrollToTop = _scrollToTop.asSharedFlow()

    init {
        collectTodayQuestion()
        collectQuestionAnswer()
    }

    fun setInQuestionTop(isTop: Boolean) {
        isInQuestionTop.value = isTop
    }

    fun setScrollToTop() = viewModelScope.launch {
        _scrollToTop.emit(true)
    }


    /** Page Modification **/
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
        TodayQuestionObserver
            .getInstance()
            .setTodayQuestion(null)
        TodayQuestionObserver
            .getInstance()
            .todayQuestion
            .collect {
                if (it == null) return@collect
                modifyPage(PageEvents.InsertItemHeader(it))

                if (isInQuestionTop.value) {
                    launch {
                        delay(50)
                        setScrollToTop()
                    }
                }
            }
    }

    private fun collectQuestionAnswer() = viewModelScope.launch {
        QuestionAnswerObserver
            .getInstance()
            .setAnsweredQuestion(null)
        QuestionAnswerObserver
            .getInstance()
            .answeredQuestion
            .collect {
                if (it == null) return@collect

                modifyPage(PageEvents.Edit(it))
            }
    }

}