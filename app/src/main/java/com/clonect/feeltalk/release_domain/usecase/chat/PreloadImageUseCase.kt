package com.clonect.feeltalk.release_domain.usecase.chat

import com.clonect.feeltalk.release_domain.repository.chat.ChatRepository
import java.io.File

class PreloadImageUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(index: Long, url: String): Triple<File?, Int, Int> {
        return chatRepository.preloadImage(index, url)
    }
}