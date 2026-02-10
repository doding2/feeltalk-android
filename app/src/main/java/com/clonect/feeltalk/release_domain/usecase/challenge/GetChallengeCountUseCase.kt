package com.clonect.feeltalk.release_domain.usecase.challenge

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.challenge.ChallengeCountDto
import com.clonect.feeltalk.release_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class GetChallengeCountUseCase(
    private val tokenRepository: TokenRepository,
    private val challengeRepository: ChallengeRepository,
) {
    suspend operator fun invoke(): Resource<ChallengeCountDto> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return challengeRepository.getChallengeCount(accessToken)
    }
}