package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.repository.UserRepository

class BreakUpCoupleUseCase2(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Resource<StatusDto> {
        return userRepository.breakUpCouple()
    }
}