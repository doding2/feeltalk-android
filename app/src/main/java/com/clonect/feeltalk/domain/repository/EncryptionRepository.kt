package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.user.AccessToken

interface EncryptionRepository {
    suspend fun test()
    suspend fun checkKeyPairsExist(): Boolean

    suspend fun uploadMyPublicKey(accessToken: AccessToken): Resource<String>
    suspend fun loadPartnerPublicKey(accessToken: AccessToken): Resource<String>
    suspend fun uploadMyPrivateKey(accessToken: AccessToken): Resource<String>
    suspend fun loadPartnerPrivateKey(accessToken: AccessToken): Resource<String>

    suspend fun encryptMyText(message: String): Resource<String>
    suspend fun decryptMyText(digest: String): Resource<String>
    suspend fun encryptPartnerText(message: String): Resource<String>
    suspend fun decryptPartnerText(digest: String): Resource<String>
}