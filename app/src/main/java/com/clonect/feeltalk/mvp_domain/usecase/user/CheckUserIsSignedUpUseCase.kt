package com.clonect.feeltalk.mvp_domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class CheckUserIsSignedUpUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Resource<Boolean> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> Resource.Success(true)
            is Resource.Error -> Resource.Error(result.throwable)
            else -> Resource.Error(Exception("Unexpected Error Occurred."))
        }
    }
}