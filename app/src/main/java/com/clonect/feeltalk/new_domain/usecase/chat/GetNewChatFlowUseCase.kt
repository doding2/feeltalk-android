package com.clonect.feeltalk.new_domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.Chat
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.accessToken
import kotlinx.coroutines.flow.Flow

class GetNewChatFlowUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(): Flow<Chat> {
        return chatRepository.getNewChatFlow()
    }
}