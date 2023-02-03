package com.clonect.feeltalk.data.repository.encryption.datasourceImpl

import android.content.Context
import android.os.Build
import android.security.keystore.KeyProperties
import android.util.Base64
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

class EncryptionLocalDataSourceImpl(
    private val context: Context
): EncryptionLocalDataSource {

    override suspend fun checkKeyPairsExist(): Boolean {
        val dir = context.filesDir
        val myPublicKeyFile = File(dir, "my_public_key.txt")
        val myPrivateKeyFile = File(dir, "my_private_key.txt")
        val partnerPublicKeyFile = File(dir, "partner_public_key.txt")
        val partnerPrivateKeyFile = File(dir, "partner_private_key.txt")

        return myPublicKeyFile.exists() && myPrivateKeyFile.exists()
                && partnerPublicKeyFile.exists() && partnerPrivateKeyFile.exists()
    }


    override suspend fun getMyPublicKey(): PublicKey? {
        val myPublicKeyFile = File(context.filesDir, "my_public_key.txt")
        val myPublicKey = myPublicKeyFile.bufferedReader().use {
            val key = it.readLine()
            val keyBytes = Base64.decode(key, Base64.NO_WRAP)
            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            keyFactory.generatePublic(keySpec)
        }
        return myPublicKey
    }

    override suspend fun getMyPrivateKey(): PrivateKey? {
        val myPrivateKeyFile = File(context.filesDir, "my_private_key.txt")
        val myPrivateKey = myPrivateKeyFile.bufferedReader().use {
            val key = it.readLine()
            val keyBytes = Base64.decode(key, Base64.NO_WRAP)
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            keyFactory.generatePrivate(keySpec)
        }
        return myPrivateKey
    }

    override suspend fun saveMyPublicKeyToCache(publicKey: PublicKey) {
        val myPublicKeyFile = File(context.filesDir, "my_public_key.txt")
        myPublicKeyFile.bufferedWriter().use {
            val keyBytes = Base64.encode(publicKey.encoded, Base64.NO_WRAP)
            val keyString = String(keyBytes)
            it.write(keyString)
        }
    }

    override suspend fun saveMyPrivateKeyToCache(privateKey: PrivateKey) {
        val myPrivateKeyFile = File(context.filesDir, "my_private_key.txt")
        myPrivateKeyFile.bufferedWriter().use {
            val keyBytes = Base64.encode(privateKey.encoded, Base64.NO_WRAP)
            val keyString = String(keyBytes)
            it.write(keyString)
        }
    }



    override suspend fun getPartnerPublicKey(): PublicKey? {
        val partnerPublicKeyFile = File(context.filesDir, "partner_public_key.txt")
        val partnerPublicKey = partnerPublicKeyFile.bufferedReader().use {
            val key = it.readLine()
            val keyBytes = Base64.decode(key, Base64.NO_WRAP)
            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            keyFactory.generatePublic(keySpec)
        }
        return partnerPublicKey
    }

    override suspend fun getPartnerPrivateKey(): PrivateKey? {
        val partnerPrivateKeyFile = File(context.filesDir, "partner_private_key.txt")
        val partnerPrivateKey = partnerPrivateKeyFile.bufferedReader().use {
            val key = it.readLine()
            val keyBytes = Base64.decode(key, Base64.NO_WRAP)
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            keyFactory.generatePrivate(keySpec)
        }
        return partnerPrivateKey
    }

    override suspend fun savePartnerPublicKeyToCache(publicKey: PublicKey) {
        val partnerPublicKeyFile = File(context.filesDir, "partner_public_key.txt")
        partnerPublicKeyFile.bufferedWriter().use {
            val keyBytes = Base64.encode(publicKey.encoded, Base64.NO_WRAP)
            val keyString = String(keyBytes)
            it.write(keyString)
        }
    }

    override suspend fun savePartnerPrivateKeyToCache(privateKey: PrivateKey) {
        val partnerPrivateKeyFile = File(context.filesDir, "partner_private_key.txt")
        partnerPrivateKeyFile.bufferedWriter().use {
            val keyBytes = Base64.encode(privateKey.encoded, Base64.NO_WRAP)
            val keyString = String(keyBytes)
            it.write(keyString)
        }
    }


}