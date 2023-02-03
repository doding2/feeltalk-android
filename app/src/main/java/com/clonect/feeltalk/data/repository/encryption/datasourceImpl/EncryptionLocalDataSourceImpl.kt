package com.clonect.feeltalk.data.repository.encryption.datasourceImpl

import android.content.Context
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import java.security.PrivateKey
import java.security.PublicKey

class EncryptionLocalDataSourceImpl(
    private val context: Context
): EncryptionLocalDataSource {

    override suspend fun checkKeyPairsExist(): Boolean {
        TODO("Not yet implemented")
    }


    override suspend fun getMyPublicKey(): PublicKey? {
        TODO("Not yet implemented")
    }

    override suspend fun getMyPrivateKey(): PrivateKey? {
        TODO("Not yet implemented")
    }

    override suspend fun saveMyPublicKeyToCache(publicKey: PublicKey) {
        TODO("Not yet implemented")
    }

    override suspend fun saveMyPrivateKeyToCache(privateKey: PrivateKey) {
        TODO("Not yet implemented")
    }


    override suspend fun getPartnerPublicKey(): PublicKey? {
        TODO("Not yet implemented")
    }

    override suspend fun getPartnerPrivateKey(): PrivateKey? {
        TODO("Not yet implemented")
    }

    override suspend fun savePartnerPublicKeyToCache(publicKey: PublicKey) {
        TODO("Not yet implemented")
    }

    override suspend fun savePartnerPrivateKeyToCache(privateKey: PrivateKey) {
        TODO("Not yet implemented")
    }

}