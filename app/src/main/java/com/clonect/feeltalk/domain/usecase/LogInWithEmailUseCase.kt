package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.LogInEmailRequest
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.repository.UserRepository

class LogInWithEmailUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(request: LogInEmailRequest): Resource<UserInfo> {
        return userRepository.logInWithEmail(request)
    }

}