package com.clonect.feeltalk.data.repository.encryption.datasourceImpl

import android.content.Context
import android.security.keystore.KeyProperties
import android.util.Base64
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import com.clonect.feeltalk.data.utils.AppLevelEncryptHelper
import com.clonect.feeltalk.data.utils.MessageEncryptHelper
import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

class EncryptionLocalDataSourceImpl(
    private val context: Context,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
    private val messageEncryptHelper: MessageEncryptHelper
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
            val encrypted = it.readLine()
            val key = appLevelEncryptHelper.decrypt("myPublicKey", encrypted)
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
            val encrypted = it.readLine()
            val key = appLevelEncryptHelper.decrypt("myPrivateKey", encrypted)
            val keyBytes = Base64.decode(key, Base64.NO_WRAP)
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            keyFactory.generatePrivate(keySpec)
        }
        return myPrivateKey
    }

    override suspend fun saveMyPublicKeyToDatabase(publicKey: PublicKey) {
        val myPublicKeyFile = File(context.filesDir, "my_public_key.txt")
        myPublicKeyFile.bufferedWriter().use {
            val keyBytes = Base64.encode(publicKey.encoded, Base64.NO_WRAP)
            val keyString = String(keyBytes)
            val encrypted = appLevelEncryptHelper.encrypt("myPublicKey", keyString)
            it.write(encrypted)
        }
    }

    override suspend fun saveMyPrivateKeyToDatabase(privateKey: PrivateKey) {
        val myPrivateKeyFile = File(context.filesDir, "my_private_key.txt")
        myPrivateKeyFile.bufferedWriter().use {
            val keyBytes = Base64.encode(privateKey.encoded, Base64.NO_WRAP)
            val keyString = String(keyBytes)
            val encrypted = appLevelEncryptHelper.encrypt("myPrivateKey", keyString)
            it.write(encrypted)
        }
    }



    override suspend fun getPartnerPublicKey(): PublicKey? {
        val partnerPublicKeyFile = File(context.filesDir, "partner_public_key.txt")
        val partnerPublicKey = partnerPublicKeyFile.bufferedReader().use {
            val encrypted = it.readLine()
            val key = appLevelEncryptHelper.decrypt("partnerPublicKey", encrypted)
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
            val encrypted = it.readLine()
            val key = appLevelEncryptHelper.decrypt("partnerPrivateKey", encrypted)
            val keyBytes = Base64.decode(key, Base64.NO_WRAP)
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            keyFactory.generatePrivate(keySpec)
        }
        return partnerPrivateKey
    }

    override suspend fun savePartnerPublicKeyToDatabase(publicKey: PublicKey) {
        val partnerPublicKeyFile = File(context.filesDir, "partner_public_key.txt")
        partnerPublicKeyFile.bufferedWriter().use {
            val keyBytes = Base64.encode(publicKey.encoded, Base64.NO_WRAP)
            val keyString = String(keyBytes)
            val encrypted = appLevelEncryptHelper.encrypt("partnerPublicKey", keyString)
            it.write(encrypted)
        }
    }

    override suspend fun savePartnerPrivateKeyToDatabase(privateKey: PrivateKey) {
        val partnerPrivateKeyFile = File(context.filesDir, "partner_private_key.txt")
        partnerPrivateKeyFile.bufferedWriter().use {
            val keyBytes = Base64.encode(privateKey.encoded, Base64.NO_WRAP)
            val keyString = String(keyBytes)
            val encrypted = appLevelEncryptHelper.encrypt("partnerPrivateKey", keyString)
            it.write(encrypted)
        }
    }


}