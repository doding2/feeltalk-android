package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.common.PageEvents
import com.clonect.feeltalk.new_domain.usecase.challenge.GetAddChallengeFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.GetDeleteChallengeFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.GetModifyChallengeFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.GetPagingOngoingChallengeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@HiltViewModel
class OngoingViewModel @Inject constructor(
    getPagingOngoingChallengeUseCase: GetPagingOngoingChallengeUseCase,
    private val getAddChallengeFlowUseCase: GetAddChallengeFlowUseCase,
    private val getDeleteChallengeFlowUseCase: GetDeleteChallengeFlowUseCase,
    private val getModifyChallengeFlowUseCase: GetModifyChallengeFlowUseCase,
): ViewModel() {

    val ongoingChallengePagingRetryLock = Mutex()

    private val _isEmpty = MutableStateFlow(true)
    val isEmpty = _isEmpty.asStateFlow()

    init {
        collectAddChallenge()
        collectEditChallenge()
        collectDeleteChallenge()
    }

    fun setEmpty(isEmpty: Boolean) {
        _isEmpty.value = isEmpty
    }


    /** Pagination **/
    private val pageModificationEvents = MutableStateFlow<List<PageEvents<Challenge>>>(emptyList())

    private fun applyPageModification(paging: PagingData<Challenge>, event: PageEvents<Challenge>): PagingData<Challenge> {
        return when (event) {
            is PageEvents.Edit -> {
                paging.map {
                    return@map if (it.index == event.item.index)
                        it.copy(
                            title = event.item.title,
                            body = event.item.body,
                            deadline = event.item.deadline,
                            isNew = event.item.isNew
                        )
                    else
                        it
                }
            }
            is PageEvents.Remove -> {
                paging.filter { it.index != event.item.index }
            }
            is PageEvents.InsertItemFooter, is PageEvents.InsertItemHeader -> {
                paging.insertSeparators { before, after ->
                    val isAlreadyExist = before?.index == event.item.index || after?.index == event.item.index
                    val isFirst = before == null && after == null
                    val isStart = before == null && after != null
                    val isEnd = before != null && after == null
                    val isMiddle = before != null && after != null

                    return@insertSeparators if (isAlreadyExist) {
                        null
                    } else if (isFirst) {
                        event.item
                    } else if (isStart && event.item.deadline < after!!.deadline) {
                        event.item
                    } else if (isEnd && event.item.deadline >= before!!.deadline) {
                        event.item
                    } else if (isMiddle && event.item.deadline >= before!!.deadline && event.item.deadline < after!!.deadline) {
                        event.item
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun modifyPage(event: PageEvents<Challenge>) {
        pageModificationEvents.value += event
    }


    val pagingOngoingChallenge: Flow<PagingData<Challenge>> = getPagingOngoingChallengeUseCase()
        .cachedIn(viewModelScope)
        .combine(pageModificationEvents) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyPageModification(acc, event)
            }
        }



    private fun collectAddChallenge() = viewModelScope.launch {
        getAddChallengeFlowUseCase().collect {
            if (!it.isCompleted) {
                modifyPage(PageEvents.InsertItemFooter(it))
            }
        }
    }

    private fun collectDeleteChallenge() = viewModelScope.launch {
        getDeleteChallengeFlowUseCase().collect {
            if (!it.isCompleted) {
                modifyPage(PageEvents.Remove(it))
            }
        }
    }

    private fun collectEditChallenge() = viewModelScope.launch {
        getModifyChallengeFlowUseCase().collect {
            if (!it.isCompleted) {
                modifyPage(PageEvents.Edit(it))
            }
        }
    }


}