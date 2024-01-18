package com.clonect.feeltalk.new_domain.usecase.challenge

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeChatResponse
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class AddMyChallengeUseCase(
    private val tokenRepository: TokenRepository,
    private val challengeRepository: ChallengeRepository,
    private val mixpanelRepository: MixpanelRepository
) {
    suspend operator fun invoke(title: String, deadline: String, content: String): Resource<ChallengeChatResponse> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        val result = challengeRepository.addMyChallenge(accessToken, title, deadline, content)

        if (result is Resource.Success) {
            mixpanelRepository.addChallenge()
        }

        return result
    }
}