package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.dto.SendPartnerCoupleRegistrationCodeDto
import com.clonect.feeltalk.domain.repository.UserRepository

class SendPartnerCoupleRegistrationCodeUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(partnerCode: String): Resource<SendPartnerCoupleRegistrationCodeDto> {
        return userRepository.sendPartnerCoupleRegistrationCode(partnerCode)
    }

}