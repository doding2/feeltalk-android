package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetUserInfoUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(): Flow<Resource<UserInfo>> = flow {
        emit(userRepository.getUserInfo())
    }

}