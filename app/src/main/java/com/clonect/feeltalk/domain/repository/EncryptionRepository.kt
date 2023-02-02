package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource

interface EncryptionRepository {
    suspend fun checkKeyPairsExist(): Boolean
    suspend fun uploadMyPublicKey(): Resource<String>
    suspend fun loadPartnerPublicKey(): Resource<String>
    suspend fun loadPartnerPrivateKey(): Resource<String>
    suspend fun encryptMyText(message: String): Resource<String>
    suspend fun decryptMyText(digest: String): Resource<String>
    suspend fun encryptPartnerText(message: String): Resource<String>
    suspend fun decryptPartnerText(digest: String): Resource<String>
}