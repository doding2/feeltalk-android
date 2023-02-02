package com.clonect.feeltalk.domain.usecase.encryption

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.repository.EncryptionRepository

class UploadMyPublicKeyUseCase(private val encryptionRepository: EncryptionRepository) {

    suspend operator fun invoke(): Resource<String> {
        return encryptionRepository.uploadMyPublicKey()
    }

}