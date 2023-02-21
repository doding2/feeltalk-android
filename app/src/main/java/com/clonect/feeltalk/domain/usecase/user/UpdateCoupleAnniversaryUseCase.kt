package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.repository.UserRepository

class UpdateCoupleAnniversaryUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(coupleAnniversaryDate: String): Resource<StatusDto> {
        return userRepository.updateCoupleAnniversary(coupleAnniversaryDate)
    }
}