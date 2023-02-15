package com.clonect.feeltalk.data.utils

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.presentation.utils.infoLog
import java.io.Serializable
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AppLevelEncryptHelper(
    private val pref: SharedPreferences
) {
    private var appLevelKey = getAppLevelKey()

    fun encrypt(name: String, message: String): String {
        return try {
            val cipher = Cipher.getInstance(BuildConfig.APP_LEVEL_CIPHER_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, appLevelKey)

            val iv = cipher.iv
            val ivString = Base64.encodeToString(iv, Base64.NO_WRAP)
            pref.edit().putString("${name}_iv", ivString).apply()

            val dataBytes = message.toByteArray(Charsets.UTF_8)
            val encryptedByteArray = cipher.doFinal(dataBytes)
            Base64.encodeToString(encryptedByteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            infoLog("Fail to encrypt app level encryptor: ${message}")
            return message
        }
    }

    fun decrypt(name: String, digest: String): String? {
        return try {
            val ivString = pref.getString("${name}_iv", null)
            val iv = Base64.decode(ivString, Base64.NO_WRAP)

            val spec = GCMParameterSpec(128, iv)
            val cipher = Cipher.getInstance(BuildConfig.APP_LEVEL_CIPHER_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, appLevelKey, spec)

            val dataBytes = Base64.decode(digest, Base64.NO_WRAP)
            val decryptedByteArray = cipher.doFinal(dataBytes)
            decryptedByteArray.toString(Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            infoLog("Fail to decrypt app level encryptor: ${digest}")
            null
        }
    }


    fun encryptObject(name: String, dataObject: Serializable): ByteArray {
        val dataBytes = dataObject.toByteArrayFeelTalk()

        val cipher = Cipher.getInstance(BuildConfig.APP_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, appLevelKey)

        val iv = cipher.iv
        val ivString = Base64.encodeToString(iv, Base64.NO_WRAP)
        pref.edit().putString("${name}_iv", ivString).apply()

        return cipher.doFinal(dataBytes)
    }

    fun <T : Serializable> decryptObject(name: String, dataBytes: ByteArray): Serializable {
        val ivString = pref.getString("${name}_iv", null)
        val iv = Base64.decode(ivString, Base64.NO_WRAP)

        val spec = GCMParameterSpec(128, iv)
        val cipher = Cipher.getInstance(BuildConfig.APP_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, appLevelKey, spec)

        val decryptedByteArray = cipher.doFinal(dataBytes)
        return fromByteArray<T>(decryptedByteArray)
    }


    private fun getAppLevelKey(): SecretKey {
        val keyStore = KeyStore.getInstance(BuildConfig.APP_LEVEL_KEY_PROVIDER)
        keyStore.load(null)

        val secretKeyEntry: KeyStore.SecretKeyEntry? = keyStore.getEntry(BuildConfig.APP_LEVEL_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        val secretKey = secretKeyEntry?.secretKey

        return secretKey
            ?: generateAppLevelKey()
    }

    private fun generateAppLevelKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, BuildConfig.APP_LEVEL_KEY_PROVIDER)
        keyGen.init(
            KeyGenParameterSpec
                .Builder(
                    BuildConfig.APP_LEVEL_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        
        infoLog("!!!!경고!!!! 앱 레벨 AES 키가 새로 생성됨")
        
        return keyGen.generateKey()
    }

}