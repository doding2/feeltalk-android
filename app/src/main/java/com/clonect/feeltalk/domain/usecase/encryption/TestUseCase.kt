package com.clonect.feeltalk.domain.usecase.encryption

import com.clonect.feeltalk.domain.repository.EncryptionRepository

class TestUseCase(private val encryptionRepository: EncryptionRepository) {
    suspend operator fun invoke() {
        encryptionRepository.test()
    }
}