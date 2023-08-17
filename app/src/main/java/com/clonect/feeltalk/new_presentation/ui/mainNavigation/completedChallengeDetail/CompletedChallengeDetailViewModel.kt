package com.clonect.feeltalk.new_presentation.ui.mainNavigation.completedChallengeDetail

import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CompletedChallengeDetailViewModel @Inject constructor(

): ViewModel() {

    private val _challenge = MutableStateFlow<Challenge?>(null)
    val challenge = _challenge.asStateFlow()


    private val _category = MutableStateFlow(ChallengeCategory.Place)
    val category = _category.asStateFlow()

    private val _title = MutableStateFlow<String?>(null)
    val title = _title.asStateFlow()

    private val _body = MutableStateFlow<String?>(null)
    val body = _body.asStateFlow()

    private val _deadline = MutableStateFlow(Date())
    val deadline = _deadline.asStateFlow()


    fun initChallenge(challenge: Challenge) {
        _challenge.value = challenge
        _category.value = challenge.category
        _title.value = challenge.title
        _body.value = challenge.body
        _deadline.value = challenge.deadline
    }


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




    fun deleteChallenge() {

    }
}