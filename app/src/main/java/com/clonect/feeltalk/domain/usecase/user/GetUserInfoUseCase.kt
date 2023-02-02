package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.repository.UserRepository

class GetUserInfoUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(): Resource<UserInfo> {
        return userRepository.getUserInfo()
    }
}