package com.clonect.feeltalk.data.repository.encryption.datasource

import java.security.PrivateKey
import java.security.PublicKey

interface EncryptionLocalDataSource {
    suspend fun checkKeyPairsExist(): Boolean

    suspend fun getMyPublicKey(): PublicKey?
    suspend fun getMyPrivateKey(): PrivateKey?
    suspend fun saveMyPublicKeyToCache(publicKey: PublicKey)
    suspend fun saveMyPrivateKeyToCache(privateKey: PrivateKey)

    suspend fun getPartnerPublicKey(): PublicKey?
    suspend fun getPartnerPrivateKey(): PrivateKey?
    suspend fun savePartnerPublicKeyToCache(publicKey: PublicKey)
    suspend fun savePartnerPrivateKeyToCache(privateKey: PrivateKey)
}