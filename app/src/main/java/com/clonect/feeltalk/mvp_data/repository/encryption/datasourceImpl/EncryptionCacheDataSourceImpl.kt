package com.clonect.feeltalk.mvp_data.repository.encryption.datasourceImpl

import com.clonect.feeltalk.mvp_data.repository.encryption.datasource.EncryptionCacheDataSource
import java.security.PrivateKey
import java.security.PublicKey

class EncryptionCacheDataSourceImpl: EncryptionCacheDataSource {

    private var appLevelAesKey: String? = null

    private var myPublicKey: PublicKey? = null
    private var myPrivateKey: PrivateKey? = null

    private var partnerPublicKey: PublicKey? = null
    private var partnerPrivateKey: PrivateKey? = null


    override fun getAppLevelAesKey(): String? = appLevelAesKey

    override fun saveAppLevelAesKey(key: String) {
        appLevelAesKey = key
    }

    override fun getMyPublicKey(): PublicKey? = myPublicKey

    override fun getMyPrivateKey(): PrivateKey? = myPrivateKey

    override fun saveMyPublicKeyToCache(publicKey: PublicKey) {
        myPublicKey = publicKey
    }

    override fun saveMyPrivateKeyToCache(privateKey: PrivateKey) {
        myPrivateKey = privateKey
    }


    override fun getPartnerPublicKey(): PublicKey? = partnerPublicKey

    override fun getPartnerPrivateKey(): PrivateKey? = partnerPrivateKey

    override fun savePartnerPublicKeyToCache(publicKey: PublicKey) {
        partnerPublicKey = publicKey
    }

    override fun savePartnerPrivateKeyToCache(privateKey: PrivateKey) {
        partnerPrivateKey = privateKey
    }
}