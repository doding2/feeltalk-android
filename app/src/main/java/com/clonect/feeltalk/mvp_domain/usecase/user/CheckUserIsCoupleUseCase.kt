package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.user.CoupleCheckDto
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class CheckUserIsCoupleUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(): Resource<CoupleCheckDto> {
        return userRepository.checkUserInCouple()
    }

}