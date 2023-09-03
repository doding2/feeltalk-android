package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.page.PageEvents
import com.clonect.feeltalk.new_domain.usecase.challenge.GetPagingCompletedChallengeUseCase
import com.clonect.feeltalk.new_presentation.notification.observer.AddCompletedChallengeObserver
import com.clonect.feeltalk.new_presentation.notification.observer.DeleteCompletedChallengeObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletedViewModel @Inject constructor(
    getPagingCompletedChallengeUseCase: GetPagingCompletedChallengeUseCase,
): ViewModel() {

    init {
        collectAddChallenge()
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
                var isAlreadyExist = false
                paging.insertSeparators { before, after ->
                    val isFirst = before == null && after == null
                    val isEnd = before != null && after == null

                    if (!isEnd && before?.index == event.item.index || after?.index == event.item.index) {
                        isAlreadyExist = true
                    }

                    return@insertSeparators if (isAlreadyExist) {
                        null
                    } else if (isFirst) {
                        event.item
                    } else if (isEnd) {
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


    val pagingCompletedChallenge: Flow<PagingData<Challenge>> = getPagingCompletedChallengeUseCase()
        .cachedIn(viewModelScope)
        .combine(pageModificationEvents) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyPageModification(acc, event)
            }
        }



    private fun collectAddChallenge() = viewModelScope.launch {
        AddCompletedChallengeObserver
            .getInstance()
            .setChallenge(null)
        AddCompletedChallengeObserver
            .getInstance()
            .challenge
            .collect {
                if (it == null) return@collect
                modifyPage(PageEvents.InsertItemFooter(it))
            }
    }

    private fun collectDeleteChallenge() = viewModelScope.launch {
        DeleteCompletedChallengeObserver
            .getInstance()
            .setChallenge(null)
        DeleteCompletedChallengeObserver
            .getInstance()
            .challenge
            .collect {
                if (it == null) return@collect
                modifyPage(PageEvents.Remove(it))
            }
    }
}