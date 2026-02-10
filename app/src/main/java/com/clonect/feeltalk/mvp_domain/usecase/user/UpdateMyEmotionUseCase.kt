package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_data.mapper.toStringLowercase
import com.clonect.feeltalk.mvp_domain.model.data.user.Emotion
import com.clonect.feeltalk.mvp_domain.model.dto.common.StatusDto
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class UpdateMyEmotionUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(emotion: Emotion): Resource<StatusDto> {
        return userRepository.updateMyEmotion(emotion.toStringLowercase())
    }

}