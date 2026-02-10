package com.clonect.feeltalk.mvp_data.utils

import com.clonect.feeltalk.common.FeelTalkException
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.repository.EncryptionRepository

class UserLevelEncryptHelper(
    private val encryptRepository: EncryptionRepository
) {
    suspend fun encryptMyText(message: String): String {
        val encrypted = encryptRepository.encryptMyText(message)
        when (encrypted) {
            is Resource.Success -> {
                return encrypted.data
            }
            is Resource.Error -> throw FeelTalkException.EncryptionFailureException()
        }
    }

    suspend fun decryptMyText(digest: String): String {
        val decrypted = encryptRepository.decryptMyText(digest)
        return when (decrypted) {
            is Resource.Success -> {
                decrypted.data
            }
            is Resource.Error -> digest
        }
    }

    suspend fun encryptPartnerText(message: String): String {
        val encrypted = encryptRepository.encryptPartnerText(message)
        when (encrypted) {
            is Resource.Success -> {
                return encrypted.data
            }
            is Resource.Error -> throw FeelTalkException.EncryptionFailureException()
        }
    }

    suspend fun decryptPartnerText(digest: String): String {
        val decrypted = encryptRepository.decryptPartnerText(digest)
        return when (decrypted) {
            is Resource.Success -> {
                decrypted.data
            }
            is Resource.Error -> digest
        }
    }

}