package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.common.StatusDto
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class BreakUpCoupleUseCase2(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Resource<StatusDto> {
        return userRepository.breakUpCouple()
    }
}