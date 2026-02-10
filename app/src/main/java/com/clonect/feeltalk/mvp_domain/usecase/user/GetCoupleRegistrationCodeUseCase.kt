package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class GetCoupleRegistrationCodeUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(withCache: Boolean = true): Resource<String> {
        return userRepository.getCoupleRegistrationCode(withCache)
    }

}