package com.clonect.feeltalk.mvp_domain.usecase.encryption

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.repository.EncryptionRepository

class CheckKeyPairsWorkWellUseCase(
    private val encryptionRepository: EncryptionRepository
) {
    suspend operator fun invoke(): Resource<Boolean> {
        return encryptionRepository.checkKeyPairsExist()
    }
}