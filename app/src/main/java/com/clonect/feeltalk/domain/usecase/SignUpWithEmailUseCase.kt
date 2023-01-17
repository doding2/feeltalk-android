package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.data.util.Result
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailResponse
import com.clonect.feeltalk.domain.repository.UserRepository

class SignUpWithEmailUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(signUpEmailRequest: SignUpEmailRequest): Result<SignUpEmailResponse> {
        return userRepository.signUpWithEmail(signUpEmailRequest)
    }

}