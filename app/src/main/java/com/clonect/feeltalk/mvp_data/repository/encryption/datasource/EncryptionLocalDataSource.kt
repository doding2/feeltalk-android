package com.clonect.feeltalk.mvp_data.repository.encryption.datasource

import java.security.PrivateKey
import java.security.PublicKey

interface EncryptionLocalDataSource {
    suspend fun checkKeyPairsExist(): Boolean

    suspend fun getMyPublicKey(): PublicKey?
    suspend fun getMyPrivateKey(): PrivateKey?
    suspend fun saveMyPublicKeyToDatabase(publicKey: PublicKey)
    suspend fun saveMyPrivateKeyToDatabase(privateKey: PrivateKey)

    suspend fun getPartnerPublicKey(): PublicKey?
    suspend fun getPartnerPrivateKey(): PrivateKey?
    suspend fun savePartnerPublicKeyToDatabase(publicKey: PublicKey)
    suspend fun savePartnerPrivateKeyToDatabase(privateKey: PrivateKey)
}