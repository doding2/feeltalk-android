package com.clonect.feeltalk.data.utils

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.repository.EncryptionRepository
import com.clonect.feeltalk.presentation.utils.infoLog

class UserLevelEncryptHelper(
    private val encryptRepository: EncryptionRepository
) {
    suspend fun encryptMyText(message: String): String {
        val encrypted = encryptRepository.encryptMyText(message)
        when (encrypted) {
            is Resource.Success -> {
                infoLog("User Level Encryption: ${message} -> ${encrypted.data}")
                return encrypted.data
            }
            is Resource.Error -> throw encrypted.throwable
            is Resource.Loading -> throw IllegalStateException("Unexpected Error Occurred.")
        }
    }

    suspend fun decryptMyText(digest: String): String {
        val decrypted = encryptRepository.decryptMyText(digest)
        return when (decrypted) {
            is Resource.Success -> {
                infoLog("User Level Decryption: ${digest} -> ${decrypted.data}")
                decrypted.data
            }
            is Resource.Error -> digest
            is Resource.Loading -> throw IllegalStateException("Unexpected Error Occurred.")
        }
    }

    suspend fun encryptPartnerText(message: String): String {
        val encrypted = encryptRepository.encryptPartnerText(message)
        when (encrypted) {
            is Resource.Success -> {
                infoLog("User Level Encryption: ${message} -> ${encrypted.data}")
                return encrypted.data
            }
            is Resource.Error -> throw encrypted.throwable
            is Resource.Loading -> throw IllegalStateException("Unexpected Error Occurred.")
        }
    }

    suspend fun decryptPartnerText(digest: String): String {
        val decrypted = encryptRepository.decryptPartnerText(digest)
        return when (decrypted) {
            is Resource.Success -> {
                infoLog("User Level Decryption: ${digest} -> ${decrypted.data}")
                decrypted.data
            }
            is Resource.Error -> digest
            is Resource.Loading -> throw IllegalStateException("Unexpected Error Occurred.")
        }
    }

}