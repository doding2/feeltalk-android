package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.mapper.toStringLowercase
import com.clonect.feeltalk.domain.model.data.user.Emotion
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.repository.UserRepository

class UpdateMyEmotionUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(emotion: Emotion): Resource<StatusDto> {
        return userRepository.updateMyEmotion(emotion.toStringLowercase())
    }

}