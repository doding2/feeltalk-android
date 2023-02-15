package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.repository.UserRepository

class ClearAllExceptKeysUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<Boolean> {
        return userRepository.clearAllExceptKeys()
    }
}