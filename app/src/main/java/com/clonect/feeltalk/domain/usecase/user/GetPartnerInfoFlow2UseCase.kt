package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetPartnerInfoFlow2UseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Flow<Resource<UserInfo>> {
        return userRepository.getPartnerInfoFlow()
    }
}