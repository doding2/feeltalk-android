package com.clonect.feeltalk.new_domain.usecase.challenge

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.accessToken
import kotlinx.coroutines.flow.Flow

class GetDeleteChallengeFlowUseCase(
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(): Flow<Challenge> {
        return challengeRepository.getDeleteChallengeFlow()
    }
}