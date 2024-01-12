package com.clonect.feeltalk.new_domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.state

class ChangeMyChatRoomStateUseCase(
    private val tokenRepository: TokenRepository,
    private val chatRepository: ChatRepository,
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(isInChat: Boolean): Resource<Unit> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        val result = chatRepository.changeMyChatRoomState(accessToken, isInChat)

        if (result is Resource.Success) {
            mixpanelRepository.setInChatSheet(isInChat)
        }

        return result
    }
}