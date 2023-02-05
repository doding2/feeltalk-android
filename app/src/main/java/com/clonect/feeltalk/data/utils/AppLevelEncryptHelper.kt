package com.clonect.feeltalk.data.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.clonect.feeltalk.BuildConfig
import com.google.android.gms.common.util.Base64Utils
import com.google.android.gms.common.util.Hex
import okio.ByteString.Companion.encodeUtf8
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AppLevelEncryptHelper(
    private val context: Context,
    private val pref: SharedPreferences
) {
    private var appLevelKey = getAppLevelKey()

    fun encrypt(message: String): String {
        val cipher = Cipher.getInstance(BuildConfig.APP_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, appLevelKey)
        val iv = cipher.iv
        writeIV(iv)
        val encryptedByteArray = cipher.doFinal(message.encodeToByteArray())
        return Base64Utils.encode(encryptedByteArray)
    }

    fun decrypt(digest: String): String {
        val iv = readIV()
        val spec = GCMParameterSpec(128, iv)
        val cipher = Cipher.getInstance(BuildConfig.APP_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, appLevelKey, spec)
        val decryptedByteArray = cipher.doFinal(digest.encodeToByteArray())
        return Base64Utils.encode(decryptedByteArray)
    }

    private fun getAppLevelKey(): SecretKey {
        val keyStore = KeyStore.getInstance(BuildConfig.APP_LEVEL_KEY_PROVIDER)
        keyStore.load(null)
        
        val secretKeyEntry: KeyStore.SecretKeyEntry? = keyStore.getEntry(BuildConfig.APP_LEVEL_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        val secretKey = secretKeyEntry?.secretKey

        return secretKey
            ?: generateAppLevelKey()
    }

    private fun writeIV(IV: ByteArray) {
        val file = File(context.filesDir, "IV.dat")
        file.writeBytes(IV)
    }

    private fun readIV(): ByteArray {
        val file = File(context.filesDir, "IV.dat")
        return file.readBytes()
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
        return keyGen.generateKey()
    }
}