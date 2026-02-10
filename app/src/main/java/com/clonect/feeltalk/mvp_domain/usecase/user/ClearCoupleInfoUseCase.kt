package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class ClearCoupleInfoUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Resource<Boolean> {
        return userRepository.clearCoupleInfo()
    }
}