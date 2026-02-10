package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.user.UserInfo
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class GetUserInfoUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(): Resource<UserInfo> {
        return userRepository.getUserInfo()
    }
}