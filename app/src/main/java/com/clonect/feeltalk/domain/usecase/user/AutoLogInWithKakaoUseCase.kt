package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.user.AccessTokenDto
import com.clonect.feeltalk.domain.repository.UserRepository

class AutoLogInWithKakaoUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<AccessTokenDto> {
        return userRepository.autoLogInWithKakao()
    }
}