package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.user.AccessTokenDto
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class AutoLogInWithAppleUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<AccessTokenDto> {
        return userRepository.autoLogInWithApple()
    }
}