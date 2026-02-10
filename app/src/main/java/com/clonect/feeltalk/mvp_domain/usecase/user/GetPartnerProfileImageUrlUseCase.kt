package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class GetPartnerProfileImageUrlUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<String> {
        return userRepository.getPartnerProfileImageUrl()
    }
}