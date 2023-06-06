package com.clonect.feeltalk.new_presentation.ui.mainNavigation.addChallenge

import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.common.plusDayBy
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddChallengeViewModel @Inject constructor(

): ViewModel() {

    private val _category = MutableStateFlow(ChallengeCategory.one)
    val category = _category.asStateFlow()

    private val _title = MutableStateFlow<String?>(null)
    val title = _title.asStateFlow()

    private val _body = MutableStateFlow<String?>(null)
    val body = _body.asStateFlow()

    private val _deadline = MutableStateFlow(Date().plusDayBy(1))
    val deadline = _deadline.asStateFlow()


    fun setCategory(category: ChallengeCategory) {
        _category.value = category
    }

    fun setTitle(title: String?) {
        _title.value = title
    }

    fun setBody(body: String?) {
        _body.value = body
    }

    fun setDeadline(deadline: Date) {
        _deadline.value = deadline
    }


    fun addNewChallenge() {
        val challenge = Challenge(
            category = _category.value.toString(),
            title = _title.value ?: return,
            body = _body.value,
            deadline = _deadline.value,
            owner = "me",
            isCompleted = false
        )


    }
}