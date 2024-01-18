package com.clonect.feeltalk.new_domain.usecase.challenge

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class DeleteChallengeUseCase(
    private val tokenRepository: TokenRepository,
    private val challengeRepository: ChallengeRepository,
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(challenge: Challenge): Resource<Unit> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        val result = challengeRepository.deleteChallenge(accessToken, challenge)

        if (result is Resource.Success) {
            mixpanelRepository.deleteChallenge()
        }

        return result
    }
}