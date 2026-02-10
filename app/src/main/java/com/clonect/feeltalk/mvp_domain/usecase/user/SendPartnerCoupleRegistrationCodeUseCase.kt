package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.user.PartnerCodeCheckDto
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class SendPartnerCoupleRegistrationCodeUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(partnerCode: String): Resource<PartnerCodeCheckDto> {
        return userRepository.sendPartnerCoupleRegistrationCode(partnerCode)
    }

}