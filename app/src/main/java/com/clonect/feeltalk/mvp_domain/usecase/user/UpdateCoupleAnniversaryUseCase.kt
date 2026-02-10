package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.common.StatusDto
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class UpdateCoupleAnniversaryUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(coupleAnniversaryDate: String): Resource<StatusDto> {
        return userRepository.updateCoupleAnniversary(coupleAnniversaryDate)
    }
}