package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.user.AccessTokenDto

interface EncryptionRepository {
    suspend fun test()
    suspend fun checkKeyPairsExist(): Boolean

    suspend fun uploadMyPublicKey(accessToken: String): Resource<String>
    suspend fun loadPartnerPublicKey(accessToken: String): Resource<String>
    suspend fun uploadMyPrivateKey(accessToken: String): Resource<String>
    suspend fun loadPartnerPrivateKey(accessToken: String): Resource<String>

    suspend fun encryptMyText(message: String): Resource<String>
    suspend fun decryptMyText(digest: String): Resource<String>
    suspend fun encryptPartnerText(message: String): Resource<String>
    suspend fun decryptPartnerText(digest: String): Resource<String>
}