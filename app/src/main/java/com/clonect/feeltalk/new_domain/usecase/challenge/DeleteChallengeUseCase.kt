package com.clonect.feeltalk.new_domain.usecase.challenge

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class DeleteChallengeUseCase(
    private val tokenRepository: TokenRepository,
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(index: Long): Resource<Unit> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return challengeRepository.deleteChallenge(accessToken, index)
    }
}