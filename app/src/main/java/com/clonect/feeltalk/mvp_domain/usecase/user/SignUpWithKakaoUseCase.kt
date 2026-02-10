package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.user.OldSignUpDto
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class SignUpWithKakaoUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(accessToken: String, fcmToken: String): Resource<OldSignUpDto> {
        return userRepository.signUpWithKakao(accessToken, fcmToken)
    }
}