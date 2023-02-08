package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.repository.UserRepository

class UpdateUserInfoUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(nickname: String, birthDate: String, anniversary: String): Resource<String> {
        return userRepository.updateUserInfo(nickname, birthDate, anniversary)
    }

}