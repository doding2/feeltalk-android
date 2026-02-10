package com.clonect.feeltalk.release_domain.usecase.challenge

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.challenge.ShareChallengeChatResponse
import com.clonect.feeltalk.release_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.release_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository

class ShareChallengeUseCase(
    private val tokenRepository: TokenRepository,
    private val challengeRepository: ChallengeRepository,
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(index: Long): Resource<ShareChallengeChatResponse> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        val result = challengeRepository.shareChallenge(accessToken, index)

        if (result is Resource.Success) {
            mixpanelRepository.shareContent()
        }

        return result
    }
}