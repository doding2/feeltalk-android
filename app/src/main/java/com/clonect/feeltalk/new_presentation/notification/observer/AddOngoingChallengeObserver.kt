package com.clonect.feeltalk.new_presentation.notification.observer

import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddOngoingChallengeObserver {
    companion object {
        private var Instance: AddOngoingChallengeObserver? = null

        fun getInstance(): AddOngoingChallengeObserver {
            if (Instance == null) {
                Instance = AddOngoingChallengeObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }

    private val _challenge = MutableStateFlow<Challenge?>(null)
    val challenge = _challenge.asStateFlow()

    fun setChallenge(Challenge: Challenge?) {
        _challenge.value = Challenge
    }
}