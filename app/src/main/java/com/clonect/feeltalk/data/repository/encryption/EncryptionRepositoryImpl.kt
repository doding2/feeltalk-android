package com.clonect.feeltalk.data.repository.encryption

import android.security.keystore.KeyProperties
import android.util.Base64
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.FeelTalkException.NoMyKeyException
import com.clonect.feeltalk.common.FeelTalkException.NoPartnerKeyException
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionCacheDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionLocalDataSource
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.data.utils.MessageEncryptHelper
import com.clonect.feeltalk.domain.repository.EncryptionRepository
import com.clonect.feeltalk.presentation.utils.infoLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
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
    private val messageEncryptHelper: MessageEncryptHelper
): EncryptionRepository {

    private var tryCount = 0


    // 얘는 지금 안 쓰는 중
    override suspend fun getAppLevelAesKey(accessToken: String): Resource<String> {
        try {
            val cache = cacheDataSource.getAppLevelAesKey()
            cache?.let { return Resource.Success(it) }

            val remote = remoteSource.getAppLevelAesKey(accessToken).body()!!
            return Resource.Success(remote.clientAESKey)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
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

    override suspend fun uploadMyPublicKey(accessToken: String): Resource<String> {
        return try {
            val myKeyPair = generateUserLevelKeyPair()

            val publicString = Base64.encodeToString(myKeyPair.public.encoded, Base64.NO_WRAP)
            val response = remoteSource.uploadMyPublicKey(accessToken, publicString)

            val status = response.body()!!.status
            Resource.Success(status)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun loadPartnerPublicKey(accessToken: String): Resource<String> {
        return try {
            val response = remoteSource.loadPartnerPublicKey(accessToken)

            val publicKeyString = response.body()!!.publicKey
            if (tryCount >= 10) {
                tryCount = 0
                throw NullPointerException("Response body from server is null.")
            }
            if (publicKeyString == null) {
                tryCount++
                delay(Constants.EXCHANGE_KEY_WAIT_DELAY)
                return loadPartnerPublicKey(accessToken)
            }

            val keyBytes = Base64.decode(publicKeyString, Base64.NO_WRAP)
            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            val partnerPublicKey = keyFactory.generatePublic(keySpec)

            tryCount = 0
            localDataSource.savePartnerPublicKeyToDatabase(partnerPublicKey)
            cacheDataSource.savePartnerPublicKeyToCache(partnerPublicKey)
            Resource.Success(publicKeyString)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun uploadMyPrivateKey(accessToken: String): Resource<String> {
        return try {
            val myPrivateKey = getMyPrivateKey()
            val myPrivateKeyString = Base64.encodeToString(myPrivateKey.encoded, Base64.NO_WRAP)

            val encryptedResource = encryptPartnerText(myPrivateKeyString)
            val encryptedPrivateKey = when (encryptedResource) {
                is Resource.Success -> encryptedResource.data
                is Resource.Error -> throw encryptedResource.throwable
                is Resource.Loading -> return Resource.Loading(encryptedResource.isLoading)
            }

            val response = remoteSource.uploadMyPrivateKey(accessToken, encryptedPrivateKey)

            val status = response.body()!!.status
            Resource.Success(status)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    override suspend fun loadPartnerPrivateKey(accessToken: String): Resource<String> {
        return try {
            val response = remoteSource.loadPartnerPrivateKey(accessToken)

            val privateKeyString = response.body()!!.privateKey
            if (tryCount >= 10) {
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

            tryCount = 0
            localDataSource.savePartnerPrivateKeyToDatabase(partnerPrivateKey)
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
            infoLog("내 텍스트 암호화 실패: $e")
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
            infoLog("내 텍스트 복호화 실패: $e")
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
            infoLog("연인 텍스트 암호화 실패: $e")
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
            infoLog("연인 텍스트 복호화 실패: $e")
            e.printStackTrace()
            Resource.Error(e)
        }
    }

    

    /** Note: RSA, AES 모두 이용한 암호화
     *
     * - 암호화
     * 1. 128 비트짜리 데이터를 랜덤하게 생성 -> 결과값은 K라고 부름
     * 2. K를 RSA 공유키로 암호화 -> 결과값은 E라고 부름
     * 3. Message를 k를 키로 하는 AES를 이용해 암호화, 일회용 키이므로 IV는 0으로 꽉 채움 -> 결과값은 F라고 부름
     * 4. E와 F를 합쳐서 암호화된 메시지 완성
     *
     *
     * - 복호화 (기본적을는 암호화의 역순)
     * 1. RSA의 비밀키로 E로부터 K를 복호화
     * 2. K를 가지고 F를 복호화해서 Original Message를 얻어냄
     * 3. K는 이제 필요 없으니 삭제
     * */
    private fun encrypt(publicKey: PublicKey, message: String): String {
        val aesKey = getRandomAESKey()
        val encryptedAesKey = encryptAESKey(publicKey, aesKey)
        val encryptedMessage = messageEncryptHelper.encryptAES(aesKey, message)
        val encrypted = encryptedAesKey + encryptedMessage
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    private fun decrypt(privateKey: PrivateKey, digest: String): String {
        val data = Base64.decode(digest, Base64.NO_WRAP)
        val encryptedAesKey = data.take(64).toByteArray()
        val aesKey = decryptAESKey(privateKey, encryptedAesKey)
        val encryptedMessage = data.takeLast(data.size - 64).toByteArray()
        val message = messageEncryptHelper.decryptAES(aesKey, encryptedMessage)
        return message
    }

    private fun encryptAESKey(publicKey: PublicKey, aesKey: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(BuildConfig.USER_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(aesKey)
    }

    private fun decryptAESKey(privateKey: PrivateKey, digest: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(BuildConfig.USER_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(digest)
    }

    private fun getRandomAESKey(): ByteArray {
        val format = SimpleDateFormat("MMddHHmmss", Locale.getDefault())
        val date = format.format(Date())
        val key = ByteArray(16)
        Random(date.toLong()).nextBytes(key)
        return key
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
            localDataSource.savePartnerPrivateKeyToDatabase(local)
            return local
        }

        throw NoPartnerKeyException("Partner private Key does not exist in this device.")
    }


    private suspend fun generateUserLevelKeyPair(): KeyPair {
        val keyPair = initUserLevelKeyStore()
        localDataSource.saveMyPublicKeyToDatabase(keyPair.public)
        localDataSource.saveMyPrivateKeyToDatabase(keyPair.private)
        cacheDataSource.saveMyPublicKeyToCache(keyPair.public)
        cacheDataSource.saveMyPrivateKeyToCache(keyPair.private)
        return keyPair
    }

    private fun initUserLevelKeyStore(): KeyPair {
        val format = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())
        val date = format.format(Date())

        val keyGen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
        keyGen.initialize(512, SecureRandom(date.toByteArray()))
        return keyGen.genKeyPair()
    }




}