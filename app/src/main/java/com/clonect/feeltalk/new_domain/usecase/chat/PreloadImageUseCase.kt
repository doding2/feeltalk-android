package com.clonect.feeltalk.new_domain.usecase.chat

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.SendVoiceChatDto
import com.clonect.feeltalk.new_domain.repository.chat.ChatRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.accessToken
import java.io.File

class PreloadImageUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(index: Long, url: String): Triple<File?, Int, Int> {
        return chatRepository.preloadImage(index, url)
    }
}