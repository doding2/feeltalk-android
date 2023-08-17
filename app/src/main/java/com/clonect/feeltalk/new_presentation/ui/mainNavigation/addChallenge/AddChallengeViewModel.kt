package com.clonect.feeltalk.new_presentation.ui.mainNavigation.addChallenge

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
class AddChallengeViewModel @Inject constructor(

): ViewModel() {

    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()


    private val _isAddEnabled = MutableStateFlow(false)
    val isAddEnabled = _isAddEnabled.asStateFlow()


    private val _category = MutableStateFlow(ChallengeCategory.Place)
    val category = _category.asStateFlow()

    private val _title = MutableStateFlow<String?>(null)
    val title = _title.asStateFlow()

    private val _body = MutableStateFlow<String?>(null)
    val body = _body.asStateFlow()

    private val _deadline = MutableStateFlow(Date())
    val deadline = _deadline.asStateFlow()


    fun setKeyboardUp(isUp: Boolean) {
        _isKeyboardUp.value = isUp
    }


    fun setCategory(category: ChallengeCategory) {
        _category.value = category
    }

    fun setTitle(title: String?) {
        _title.value = title

        val isAddEnabled = !(title.isNullOrBlank() || body.value.isNullOrBlank())
        _isAddEnabled.value = isAddEnabled
    }

    fun setBody(body: String?) {
        _body.value = body

        val isAddEnabled = !(title.value.isNullOrBlank() || body.isNullOrBlank())
        _isAddEnabled.value = isAddEnabled
    }

    fun setDeadline(deadline: Date) {
        _deadline.value = deadline
    }

    fun isEdited(): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return category.value != ChallengeCategory.Place
                || !title.value.isNullOrBlank()
                || !body.value.isNullOrBlank()
                || format.format(deadline.value) != format.format(Date())
    }


    fun addNewChallenge() {
        val challenge = Challenge(
            category = _category.value,
            title = _title.value ?: return,
            body = _body.value ?: return,
            deadline = _deadline.value,
            owner = "me",
            isCompleted = false
        )


    }
}