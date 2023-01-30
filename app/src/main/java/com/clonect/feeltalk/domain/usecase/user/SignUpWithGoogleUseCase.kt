package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.AccessToken
import com.clonect.feeltalk.domain.repository.UserRepository

class SignUpWithGoogleUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(idToken: String, serverAuthCode: String): Resource<AccessToken> {
        return userRepository.signUpWithGoogle(idToken, serverAuthCode)
    }

}