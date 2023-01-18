package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.UserInfo
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.repository.UserRepository

class SignUpWithEmailUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(request: SignUpEmailRequest): Resource<UserInfo> {
        return userRepository.signUpWithEmail(request)
    }

}