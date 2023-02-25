package com.clonect.feeltalk.domain.usecase.encryption

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.repository.EncryptionRepository

class CheckKeyPairsWorkWellUseCase(
    private val encryptionRepository: EncryptionRepository
) {
    suspend operator fun invoke(): Resource<Boolean> {
        return encryptionRepository.checkKeyPairsExist()
    }
}