package com.clonect.feeltalk.data.repository.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.common.FeelTalkException.*
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionCacheDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.domain.repository.EncryptionRepository
import com.google.android.gms.common.util.Base64Utils
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.security.*
import java.security.spec.RSAKeyGenParameterSpec
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher

class EncryptionRepositoryImpl(
    private val remoteSource: EncryptionRemoteDataSource,
    private val localDataSource: EncryptionLocalDataSource,
    private val cacheDataSource: EncryptionCacheDataSource,
): EncryptionRepository {

    override suspend fun test() {
        val myKeyPair = generateUserLevelKeyPair()

        val myPublicKey = localDataSource.getMyPublicKey()
        val myPrivateKey = localDataSource.getMyPrivateKey()


        val publicBytes = Base64.encode(myPublicKey?.encoded, Base64.DEFAULT)
        val publicString = String(publicBytes)

        val privateBytes = Base64.encode(myPrivateKey?.encoded, Base64.DEFAULT)
        val privateString = String(privateBytes)

        Log.i("Fragment", "local public key: ${publicString}")
        Log.i("Fragment", "local private key: ${privateString}")
    }

    override suspend fun checkKeyPairsExist(): Boolean {
        return try {
            val isExist = localDataSource.checkKeyPairsExist()
            isExist
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun uploadMyPublicKey(): Resource<String> {
        return try {
            val myKeyPair = generateUserLevelKeyPair()

            val response = remoteSource.uploadMyPublicKey(myKeyPair.public)
            if (!response.isSuccessful) throw HttpException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")

            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun loadPartnerPublicKey(): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun loadPartnerPrivateKey(): Resource<String> {
        TODO("Not yet implemented")
    }


    override suspend fun encryptMyText(message: String): Resource<String> {
        return try {
            val publicKey = getMyPublicKey()
            val encrypted = encrypt(publicKey, message)
            Resource.Success(encrypted)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun decryptMyText(digest: String): Resource<String> {
        return try {
            val privateKey = getMyPrivateKey()
            val decrypted = decrypt(privateKey, digest)
            Resource.Success(decrypted)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun encryptPartnerText(message: String): Resource<String> {
        return try {
            val publicKey = getPartnerPublicKey()
            val encrypted = encrypt(publicKey, message)
            Resource.Success(encrypted)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun decryptPartnerText(digest: String): Resource<String> {
        return try {
            val privateKey = getPartnerPrivateKey()
            val decrypted = decrypt(privateKey, digest)
            Resource.Success(decrypted)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    private fun encrypt(publicKey: PublicKey, digest: String): String {
        val cipher = Cipher.getInstance(BuildConfig.CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedByteArray = cipher.doFinal(digest.toByteArray())
        return Base64Utils.encode(encryptedByteArray)
    }

    private fun decrypt(privateKey: PrivateKey, digest: String): String {
        val cipher = Cipher.getInstance(BuildConfig.CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedByteArray = cipher.doFinal(digest.toByteArray())
        return Base64Utils.encode(decryptedByteArray)
    }


    private suspend fun getMyPublicKey(): PublicKey {
        val cache = cacheDataSource.getMyPublicKey()
        if (cache != null) return cache

        val local = localDataSource.getMyPublicKey()
        if (local != null) return local

        throw NoMyKeyException("My public Key does not exist in this device.")
    }

    private suspend fun getMyPrivateKey(): PrivateKey {
        val cache = cacheDataSource.getMyPrivateKey()
        if (cache != null) return cache

        val local = localDataSource.getMyPrivateKey()
        if (local != null) return local

        throw NoMyKeyException("My private Key does not exist in this device.")
    }

    private suspend fun getPartnerPublicKey(): PublicKey {
        val cache = cacheDataSource.getPartnerPublicKey()
        if (cache != null) return cache

        val local = localDataSource.getPartnerPublicKey()
        if (local != null) return local

        throw NoPartnerKeyException("Partner public Key does not exist in this device.")
    }

    private suspend fun getPartnerPrivateKey(): PrivateKey {
        val cache = cacheDataSource.getPartnerPrivateKey()
        if (cache != null) return cache

        val local = localDataSource.getPartnerPrivateKey()
        if (local != null) return local

        throw NoPartnerKeyException("Partner private Key does not exist in this device.")
    }


    private suspend fun generateUserLevelKeyPair(): KeyPair {
        val keyPair = initUserLevelKeyStore()
        localDataSource.saveMyPublicKeyToCache(keyPair.public)
        localDataSource.saveMyPrivateKeyToCache(keyPair.private)
        cacheDataSource.saveMyPublicKeyToCache(keyPair.public)
        cacheDataSource.saveMyPrivateKeyToCache(keyPair.private)
        return keyPair
    }

    private fun initUserLevelKeyStore(): KeyPair {
        val format = SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss", Locale.getDefault())
        val date = format.format(Date())

        val keyGen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
        keyGen.initialize(2048, SecureRandom(date.toByteArray()))
        return keyGen.genKeyPair()
    }




}