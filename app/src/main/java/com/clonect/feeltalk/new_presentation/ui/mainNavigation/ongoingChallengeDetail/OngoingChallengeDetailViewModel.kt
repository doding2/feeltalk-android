package com.clonect.feeltalk.new_presentation.ui.mainNavigation.ongoingChallengeDetail

import androidx.lifecycle.ViewModel
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OngoingChallengeDetailViewModel @Inject constructor(

) : ViewModel() {

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()



    private val _challenge = MutableStateFlow<Challenge?>(null)
    val challenge = _challenge.asStateFlow()


    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    private val _isEditEnabled = MutableStateFlow(true)
    val isEditEnabled = _isEditEnabled.asStateFlow()


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

    fun setEditMode(enabled: Boolean) {
        _isEditMode.value = enabled
        if (!enabled) {
            setKeyboardUp(false)
        }
    }


    fun setKeyboardUp(isUp: Boolean) {
        _isKeyboardUp.value = isUp
    }


    fun setCategory(category: ChallengeCategory) {
        _category.value = category
    }

    fun setTitle(title: String?) {
        _title.value = title

        val isEditEnabled = !(title.isNullOrBlank() || body.value.isNullOrBlank())
        _isEditEnabled.value = isEditEnabled
    }

    fun setBody(body: String?) {
        _body.value = body

        val isEditEnabled = !(title.value.isNullOrBlank() || body.isNullOrBlank())
        _isEditEnabled.value = isEditEnabled
    }

    fun setDeadline(deadline: Date) {
        _deadline.value = deadline
    }


    fun isEdited(): Boolean = challenge.value.let {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        it?.title != title.value
                || it?.body != body.value
                || it?.category != category.value
                || format.format(it.deadline) != format.format(deadline.value)
    }



    fun completeChallenge() {

    }

    fun editChallenge() {
        val title = _title.value ?: return
        val body = _body.value ?: return

        _challenge.value = _challenge.value?.copy(
            category = _category.value,
            title = title,
            body = body,
            deadline = deadline.value
        )
    }

    fun deleteChallenge() {

    }
}