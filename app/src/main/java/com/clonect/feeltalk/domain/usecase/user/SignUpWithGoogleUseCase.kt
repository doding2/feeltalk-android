package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.user.AccessTokenDto
import com.clonect.feeltalk.domain.model.dto.user.SignUpDto
import com.clonect.feeltalk.domain.repository.UserRepository

class SignUpWithGoogleUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(idToken: String, serverAuthCode: String, fcmToken: String): Resource<SignUpDto> {
        return userRepository.signUpWithGoogle(idToken, serverAuthCode, fcmToken)
    }

}