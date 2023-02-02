package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.domain.repository.UserRepository

class RemoveCoupleRegistrationCodeUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke() {
        userRepository.removeCoupleRegistrationCode()
    }

}