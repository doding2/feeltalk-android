package com.clonect.feeltalk.data.utils

import com.clonect.feeltalk.domain.repository.EncryptionRepository

class UserLevelEncryptHelper(
    private val encryptRepository: EncryptionRepository
) {
    suspend fun encryptMyText(message: String) = encryptRepository.encryptMyText(message)
    suspend fun decryptMyText(digest: String) = encryptRepository.decryptMyText(digest)

    suspend fun encryptPartnerText(message: String) = encryptRepository.encryptPartnerText(message)
    suspend fun decryptPartnerText(digest: String) = encryptRepository.decryptPartnerText(digest)
}