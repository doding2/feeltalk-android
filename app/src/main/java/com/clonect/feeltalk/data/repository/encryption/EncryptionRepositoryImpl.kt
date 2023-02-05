package com.clonect.feeltalk.data.repository.encryption

import android.os.Build.VERSION_CODES.N
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.FeelTalkException.*
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionCacheDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.domain.model.user.AccessToken
import com.clonect.feeltalk.domain.repository.EncryptionRepository
import com.google.android.gms.common.util.Base64Utils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher

class EncryptionRepositoryImpl(
    private val remoteSource: EncryptionRemoteDataSource,
    private val localDataSource: EncryptionLocalDataSource,
    private val cacheDataSource: EncryptionCacheDataSource,
): EncryptionRepository {

    private var tryCount = 0

    override suspend fun test() {
        generateUserLevelKeyPair()

        val myPublicKey = localDataSource.getMyPublicKey()
        val myPrivateKey = localDataSource.getMyPrivateKey()

        val publicBytes = Base64.encode(myPublicKey?.encoded, Base64.DEFAULT)
        val publicString = String(publicBytes)

        val privateBytes = Base64.encode(myPrivateKey?.encoded, Base64.DEFAULT)
        val privateString = String(privateBytes)

        Log.i("Fragment", "local public key: $publicString")
        Log.i("Fragment", "local private key: $privateString")
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

    override suspend fun uploadMyPublicKey(accessToken: AccessToken): Resource<String> {
        return try {
            val myKeyPair = generateUserLevelKeyPair()

            val response = remoteSource.uploadMyPublicKey(accessToken, myKeyPair.public)
            if (!response.isSuccessful) throw HttpException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")

            val status = response.body()!!.status
            Resource.Success(status)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun loadPartnerPublicKey(accessToken: AccessToken): Resource<String> {
        return try {
            val response = remoteSource.loadPartnerPublicKey(accessToken)
            if (!response.isSuccessful) throw HttpException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")

            val publicKeyString = response.body()!!.publicKey
            if (tryCount >= 5) {
                tryCount = 0
                throw NullPointerException("Response body from server is null.")
            }
            if (publicKeyString == null) {
                tryCount++
                delay(Constants.EXCHANGE_KEY_WAIT_DELAY)
                return loadPartnerPublicKey(accessToken)
            }

            val keyBytes = Base64.decode(publicKeyString as String, Base64.NO_WRAP)
            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            val partnerPublicKey = keyFactory.generatePublic(keySpec)

            localDataSource.savePartnerPublicKeyToCache(partnerPublicKey)
            cacheDataSource.savePartnerPublicKeyToCache(partnerPublicKey)
            tryCount = 0
            Resource.Success(publicKeyString)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun uploadMyPrivateKey(accessToken: AccessToken): Resource<String> {
        return try {
            val myPrivateKey = getMyPrivateKey()
            val myPrivateKeyBytes = Base64.encode(myPrivateKey.encoded, Base64.NO_WRAP)
            val myPrivateKeyString = String(myPrivateKeyBytes)

            val encryptedResource = encryptPartnerText(myPrivateKeyString)
            val encryptedPrivateKey = when (encryptedResource) {
                is Resource.Success -> encryptedResource.data
                is Resource.Error -> throw encryptedResource.throwable
                is Resource.Loading -> return Resource.Loading(encryptedResource.isLoading)
            }

            val response = remoteSource.uploadMyPrivateKey(accessToken, encryptedPrivateKey)
            if (!response.isSuccessful) throw HttpException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")

            val status = response.body()!!.status
            Resource.Success(status)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun loadPartnerPrivateKey(accessToken: AccessToken): Resource<String> {
        return try {
            val response = remoteSource.loadPartnerPrivateKey(accessToken)
            if (!response.isSuccessful) throw HttpException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")

            val privateKeyString = response.body()!!.privateKey
            if (tryCount >= 5) {
                tryCount = 0
                throw NullPointerException("Response body from server is null.")
            }
            if (privateKeyString == null) {
                tryCount++
                delay(Constants.EXCHANGE_KEY_WAIT_DELAY)
                return loadPartnerPrivateKey(accessToken)
            }

            val decryptedResource = decryptMyText(privateKeyString)
            val decryptedPrivateKey = when (decryptedResource) {
                is Resource.Success -> decryptedResource.data
                is Resource.Error -> throw decryptedResource.throwable
                is Resource.Loading -> return Resource.Loading(decryptedResource.isLoading)
            }

            val keyBytes = Base64.decode(decryptedPrivateKey, Base64.NO_WRAP)
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            val partnerPrivateKey = keyFactory.generatePrivate(keySpec)

            localDataSource.savePartnerPrivateKeyToCache(partnerPrivateKey)
            cacheDataSource.savePartnerPrivateKeyToCache(partnerPrivateKey)
            Resource.Success(privateKeyString)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun encryptMyText(message: String): Resource<String> {
        return try {
            val publicKey = getMyPublicKey()
            val encrypted = encrypt(publicKey, message)
            Resource.Success(encrypted)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
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
            e.printStackTrace()
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
            e.printStackTrace()
            Resource.Error(e)
        }
    }


    private fun encrypt(publicKey: PublicKey, message: String): String {
        val cipher = Cipher.getInstance(BuildConfig.USER_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedByteArray = cipher.doFinal(message.toByteArray())
        return Base64.encodeToString(encryptedByteArray, Base64.NO_WRAP)
    }

    private fun decrypt(privateKey: PrivateKey, digest: String): String {
        val cipher = Cipher.getInstance(BuildConfig.USER_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedByteArray = cipher.doFinal(Base64.decode(digest, Base64.NO_WRAP))
        return String(decryptedByteArray)
    }


    private suspend fun getMyPublicKey(): PublicKey {
        val cache = cacheDataSource.getMyPublicKey()
        if (cache != null) return cache

        val local = localDataSource.getMyPublicKey()
        if (local != null) {
            cacheDataSource.saveMyPublicKeyToCache(local)
            return local
        }

        throw NoMyKeyException("My public Key does not exist in this device.")
    }

    private suspend fun getMyPrivateKey(): PrivateKey {
        val cache = cacheDataSource.getMyPrivateKey()
        if (cache != null) return cache

        val local = localDataSource.getMyPrivateKey()
        if (local != null) {
            cacheDataSource.saveMyPrivateKeyToCache(local)
            return local
        }

        throw NoMyKeyException("My private Key does not exist in this device.")
    }

    private suspend fun getPartnerPublicKey(): PublicKey {
        val cache = cacheDataSource.getPartnerPublicKey()
        if (cache != null) return cache

        val local = localDataSource.getPartnerPublicKey()
        if (local != null) {
            cacheDataSource.savePartnerPublicKeyToCache(local)
            return local
        }

        throw NoPartnerKeyException("Partner public Key does not exist in this device.")
    }

    private suspend fun getPartnerPrivateKey(): PrivateKey {
        val cache = cacheDataSource.getPartnerPrivateKey()
        if (cache != null) return cache

        val local = localDataSource.getPartnerPrivateKey()
        if (local != null) {
            localDataSource.savePartnerPrivateKeyToCache(local)
            return local
        }

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
        keyGen.initialize(4096, SecureRandom(date.toByteArray()))
        return keyGen.genKeyPair()
    }




}