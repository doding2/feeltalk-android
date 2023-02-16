package com.clonect.feeltalk.data.repository.encryption.datasource

import java.security.PrivateKey
import java.security.PublicKey

interface EncryptionCacheDataSource {
    fun getAppLevelAesKey(): String?
    fun saveAppLevelAesKey(key: String)

    fun getMyPublicKey(): PublicKey?
    fun getMyPrivateKey(): PrivateKey?
    fun saveMyPublicKeyToCache(publicKey: PublicKey)
    fun saveMyPrivateKeyToCache(privateKey: PrivateKey)

    fun getPartnerPublicKey(): PublicKey?
    fun getPartnerPrivateKey(): PrivateKey?
    fun savePartnerPublicKeyToCache(publicKey: PublicKey)
    fun savePartnerPrivateKeyToCache(privateKey: PrivateKey)
}