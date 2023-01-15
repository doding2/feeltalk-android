package com.clonect.feeltalk.domain.usecase

import com.clonect.feeltalk.data.util.Resource
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailResponse
import com.clonect.feeltalk.domain.repository.UserRepository
import retrofit2.Response

class SignUpWithEmail(private val userRepository: UserRepository) {

    suspend operator fun invoke(signUpEmailRequest: SignUpEmailRequest): Resource<SignUpEmailResponse> {
        return userRepository.signUpWithEmail(signUpEmailRequest)
    }

}