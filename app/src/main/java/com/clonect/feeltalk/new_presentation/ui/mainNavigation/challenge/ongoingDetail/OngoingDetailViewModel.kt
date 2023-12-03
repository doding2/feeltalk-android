package com.clonect.feeltalk.new_presentation.ui.mainNavigation.challenge.ongoingDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import com.clonect.feeltalk.new_domain.model.chat.CompleteChallengeChat
import com.clonect.feeltalk.new_domain.usecase.challenge.CompleteChallengeUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.DeleteChallengeUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.GetModifyChallengeFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.challenge.ModifyChallengeUseCase
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
class OngoingDetailViewModel @Inject constructor(
    private val modifyChallengeUseCase: ModifyChallengeUseCase,
    private val deleteChallengeUseCase: DeleteChallengeUseCase,
    private val completeChallengeUseCase: CompleteChallengeUseCase,
    private val getModifyChallengeFlowUseCase: GetModifyChallengeFlowUseCase,
    private val addNewChatCacheUseCase: AddNewChatCacheUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _isKeyboardUp = MutableStateFlow(false)
    val isKeyboardUp = _isKeyboardUp.asStateFlow()


    private val _isChallengeCompleted = MutableStateFlow(false)
    val isChallengeCompleted = _isChallengeCompleted.asStateFlow()


    private val _challenge = MutableStateFlow<Challenge?>(null)
    val challenge = _challenge.asStateFlow()


    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    private val _isEditEnabled = MutableStateFlow(true)
    val isEditEnabled = _isEditEnabled.asStateFlow()


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


    init {
        collectEditOngoingChallenge()
    }


    fun initChallenge(challenge: Challenge) {
        _challenge.value = challenge
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

    fun setFocusedEditText(et: String?) {
        _focused.value = et
    }

    fun setChallengeCompleted(isCompleted: Boolean) {
        _isChallengeCompleted.value = isCompleted
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
                || format.format(it?.deadline) != format.format(deadline.value)
    }



    fun completeChallenge(onSuccess: () -> Unit) = viewModelScope.launch {
        val challenge = _challenge.value ?: return@launch
        _isLoading.value = true
        when (val result = completeChallengeUseCase(challenge)) {
            is Resource.Success -> {
                val challengeFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())
                addNewChatCacheUseCase(
                    result.data.run {
                        CompleteChallengeChat(
                            index = index,
                            pageNo = pageIndex,
                            chatSender = "me",
                            isRead = isRead,
                            createAt = createAt,
                            challenge = Challenge(
                                index = coupleChallenge.index,
                                title = coupleChallenge.challengeTitle,
                                body = coupleChallenge.challengeBody,
                                deadline = challengeFormat.parse(coupleChallenge.deadline) ?: challenge.deadline,
                                owner = coupleChallenge.creator,
                                isCompleted = true,
                                isNew = challenge.isNew
                            )
                        )
                    }
                )
                onSuccess()
            }
            is Resource.Error -> {
                infoLog("Fail to complete challenge: ${result.throwable.localizedMessage}")
            }
        }
        _isLoading.value = false
    }

    fun editChallenge(onSuccess: () -> Unit) = viewModelScope.launch {
        _isLoading.value = true
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val index = _challenge.value?.index ?: run {
            _isLoading.value = false
            return@launch
        }
        val owner = _challenge.value?.owner ?: run {
            _isLoading.value = false
            return@launch
        }
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

        when (val result = modifyChallengeUseCase(index, title, deadline, content, owner)) {
            is Resource.Success -> {
                onSuccess()
            }
            is Resource.Error -> {
                infoLog("Fail to edit challenge: ${result.throwable.localizedMessage}")
            }
        }
        _isLoading.value = false
    }

    fun deleteChallenge(onSuccess: () -> Unit) = viewModelScope.launch {
        val challenge = _challenge.value ?: return@launch
        _isLoading.value = true
        when (val result = deleteChallengeUseCase(challenge)) {
            is Resource.Success -> {
                onSuccess()
            }
            is Resource.Error -> {
                infoLog("Fail to delete ongoing challenge: ${result.throwable.localizedMessage}")
            }
        }
        _isLoading.value = false
    }



    private fun collectEditOngoingChallenge() = viewModelScope.launch {
        getModifyChallengeFlowUseCase().collect { new ->
            val old = _challenge.value ?: return@collect
            if (new.index != old.index) return@collect

            val edited = old.copy(
                title = new.title,
                body = new.body,
                deadline = new.deadline,
            )
            initChallenge(edited)
        }
    }

}