package com.clonect.feeltalk.release_presentation.ui.mainNavigation.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertFooterItem
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.clonect.feeltalk.common.PageEvents
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.chat.PokeChat
import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.usecase.chat.AddNewChatCacheUseCase
import com.clonect.feeltalk.release_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.release_domain.usecase.mixpanel.SetInQuestionPageMixpanelUseCase
import com.clonect.feeltalk.release_domain.usecase.question.GetAnswerQuestionFlowUseCase
import com.clonect.feeltalk.release_domain.usecase.question.GetPagingQuestionUseCase
import com.clonect.feeltalk.release_domain.usecase.question.GetTodayQuestionFlowUseCase
import com.clonect.feeltalk.release_domain.usecase.question.PressForAnswerUseCase
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    getPagingQuestionUseCase: GetPagingQuestionUseCase,
    private val pressForAnswerUseCase: PressForAnswerUseCase,
    private val getAnswerQuestionFlowUseCase: GetAnswerQuestionFlowUseCase,
    private val getTodayQuestionFlowUseCase: GetTodayQuestionFlowUseCase,
    private val addNewChatCacheUseCase: AddNewChatCacheUseCase,
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
    private val setInQuestionPageMixpanelUseCase: SetInQuestionPageMixpanelUseCase,
    ) : ViewModel() {

    val questionPagingRetryLock = Mutex()

    private val isInQuestionTop = MutableStateFlow(true)

    private val _scrollToTop = MutableStateFlow(false)
    val scrollToTop = _scrollToTop.asStateFlow()

    init {
        collectTodayQuestion()
        collectQuestionAnswer()
    }

    fun pressForAnswer(index: Long, onComplete: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        when (val result = pressForAnswerUseCase(index)) {
            is Resource.Success -> {
                addNewChatCacheUseCase(
                    result.data.run {
                        PokeChat(
                            index = this.index,
                            pageNo = pageIndex,
                            chatSender = "me",
                            isRead = isRead,
                            createAt = createAt,
                            questionIndex = index
                        )
                    }
                )
                onComplete()
            }
            is Resource.Error -> {
                infoLog("Fail to answer question: ${result.throwable.localizedMessage}\n${result.throwable.stackTrace.joinToString("\n")}")
            }
        }
    }

    fun setInQuestionTop(isTop: Boolean) {
        isInQuestionTop.value = isTop
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

            if (isInQuestionTop.value) {
                setScrollToTop(true)
            }
        }
    }

    private fun collectQuestionAnswer() = viewModelScope.launch {
        getAnswerQuestionFlowUseCase().collect {
            modifyPage(PageEvents.Edit(it))
        }
    }


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }

    fun setInQuestionPage(isInQuestion: Boolean) = viewModelScope.launch {
        setInQuestionPageMixpanelUseCase(isInQuestion)
    }
}