package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.user.SignUpDto
import com.clonect.feeltalk.domain.repository.UserRepository

class SignUpWithAppleUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uuid: String, fcmToken: String): Resource<SignUpDto> {
        return userRepository.signUpWithApple(uuid, fcmToken)
    }
}