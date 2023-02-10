package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.repository.UserRepository

class GetCoupleAnniversaryUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Resource<String> {
        return userRepository.getCoupleAnniversary()
    }
}