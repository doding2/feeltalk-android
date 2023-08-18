package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.page.PageEvents
import com.clonect.feeltalk.new_domain.usecase.challenge.GetPagingOngoingChallengeUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.AddOngoingChallengeObserver
import com.clonect.feeltalk.new_presentation.notification.observer.DeleteOngoingChallengeObserver
import com.clonect.feeltalk.new_presentation.notification.observer.EditOngoingChallengeObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OngoingChallengeViewModel @Inject constructor(
    getPagingOngoingChallengeUseCase: GetPagingOngoingChallengeUseCase,
): ViewModel() {

    init {
        collectAddChallenge()
        collectEditChallenge()
        collectDeleteChallenge()
    }

    /** Pagination **/
    private val pageModificationEvents = MutableStateFlow<List<PageEvents<Challenge>>>(emptyList())

    private suspend fun applyPageModification(paging: PagingData<Challenge>, event: PageEvents<Challenge>): PagingData<Challenge> {
        return when (event) {
            is PageEvents.Edit -> {
                paging.map {
                    return@map if (it.index == event.item.index)
                        it.copy(
                            category = event.item.category,
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
                var count = pagingOngoingChallenge.count()

                paging.insertSeparators { before, after ->
                    val newDeadline = event.item.deadline
                    val beforeDeadline = before?.deadline
                    val afterDeadline = after?.deadline
                    return@insertSeparators if (beforeDeadline == null && afterDeadline == null) {
                        event.item
                    } else if (count == 1 && beforeDeadline == null && newDeadline <= afterDeadline) {
                        count++
                        event.item
                    } else if (count == 1 && afterDeadline == null && newDeadline >= beforeDeadline) {
                        count++
                        event.item
                    } else if (beforeDeadline != null && afterDeadline != null && newDeadline >= beforeDeadline && newDeadline <= afterDeadline) {
                        event.item
                    } else {
                        null
                    }
                }
            }
        }
    }

    private fun modifyPage(event: PageEvents<Challenge>) {
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