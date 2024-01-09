package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.addChallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import com.clonect.feeltalk.new_domain.model.chat.AddChallengeChat
import com.clonect.feeltalk.new_domain.usecase.challenge.AddMyChallengeUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.AddNewChatCacheUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddChallengeViewModel @Inject constructor(
    private val addMyChallengeUseCase: AddMyChallengeUseCase,
    private val addNewChatCacheUseCase: AddNewChatCacheUseCase,
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()

    private val _isAddEnabled = MutableStateFlow(false)
    val isAddEnabled = _isAddEnabled.asStateFlow()


    private val _focused = MutableStateFlow<String?>(null)
    val focused = _focused.asStateFlow()


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


    fun setFocusedEditText(et: String?) {
        _focused.value = et
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


    fun addNewChallenge(onSuccess: () -> Unit) = viewModelScope.launch {
        _isLoading.value = true
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val title = title.value ?: run {
            _isLoading.value = false
            return@launch
        }
        val content = body.value ?: run {
            _isLoading.value = false
            return@launch
        }
        val deadlineDate = deadline.value
        val deadline = format.format(deadlineDate)
        when (val result = addMyChallengeUseCase(title, deadline, content)) {
            is Resource.Success -> {
                val challengeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                addNewChatCacheUseCase(
                    result.data.run {
                        AddChallengeChat(
                            index = index,
                            pageNo = pageIndex,
                            chatSender = "me",
                            isRead = isRead,
                            createAt = createAt,
                            challenge = Challenge(
                                index = coupleChallenge.index,
                                title = coupleChallenge.challengeTitle,
                                body = coupleChallenge.challengeBody,
                                deadline = challengeFormat.parse(coupleChallenge.deadline) ?: deadlineDate,
                                owner = coupleChallenge.creator,
                                isCompleted = false,
                                isNew = true
                            )
                        )
                    }
                )
                onSuccess()
            }
            is Resource.Error -> {
                infoLog("Fail to add challenge: ${result.throwable.localizedMessage}")
            }
        }
        _isLoading.value = false
    }
}