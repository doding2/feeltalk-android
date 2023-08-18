package com.clonect.feeltalk.new_domain.usecase.challenge

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeDto
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class GetChallengeUseCase(
    private val tokenRepository: TokenRepository,
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(index: Long): Resource<ChallengeDto> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return challengeRepository.getChallenge(accessToken, index)
    }
}