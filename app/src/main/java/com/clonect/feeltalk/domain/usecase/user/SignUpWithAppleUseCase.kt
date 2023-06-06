package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.user.OldSignUpDto
import com.clonect.feeltalk.domain.repository.UserRepository

class SignUpWithAppleUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uuid: String, fcmToken: String): Resource<OldSignUpDto> {
        return userRepository.signUpWithApple(uuid, fcmToken)
    }
}