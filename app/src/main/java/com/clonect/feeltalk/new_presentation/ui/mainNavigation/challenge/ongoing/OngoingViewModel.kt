package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.common.PageEvents
import com.clonect.feeltalk.new_domain.usecase.challenge.GetPagingOngoingChallengeUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.AddOngoingChallengeObserver
import com.clonect.feeltalk.new_presentation.notification.observer.DeleteOngoingChallengeObserver
import com.clonect.feeltalk.new_presentation.notification.observer.EditOngoingChallengeObserver
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OngoingViewModel @Inject constructor(
    getPagingOngoingChallengeUseCase: GetPagingOngoingChallengeUseCase,
): ViewModel() {

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
                            deadline = event.item.deadline
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
        AddOngoingChallengeObserver
            .getInstance()
            .setChallenge(null)
        AddOngoingChallengeObserver
            .getInstance()
            .challenge
            .collect {
                if (it == null) return@collect
                modifyPage(PageEvents.InsertItemFooter(it))
            }
    }

    private fun collectDeleteChallenge() = viewModelScope.launch {
        DeleteOngoingChallengeObserver
            .getInstance()
            .setChallenge(null)
        DeleteOngoingChallengeObserver
            .getInstance()
            .challenge
            .collect {
                if (it == null) return@collect
                modifyPage(PageEvents.Remove(it))
            }
    }

    private fun collectEditChallenge() = viewModelScope.launch {
        EditOngoingChallengeObserver
            .getInstance()
            .setChallenge(null)
        EditOngoingChallengeObserver
            .getInstance()
            .challenge
            .collect {
                if (it == null) return@collect
                modifyPage(PageEvents.Edit(it))
            }
    }


}