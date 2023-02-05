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
import okio.utf8Size
import java.io.File
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
        val cipher = Cipher.getInstance(BuildConfig.APP_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, appLevelKey)

        val iv = cipher.iv
        val ivString = Base64.encodeToString(iv, Base64.NO_WRAP)
        pref.edit().putString("${name}_iv", ivString).apply()

        val encryptedByteArray = cipher.doFinal(message.toByteArray())
        return Base64.encodeToString(encryptedByteArray, Base64.NO_WRAP)
    }

    fun decrypt(name: String, digest: String): String {
        val ivString = pref.getString("${name}_iv", null)
        val iv = Base64.decode(ivString, Base64.NO_WRAP)

        val spec = GCMParameterSpec(128, iv)
        val cipher = Cipher.getInstance(BuildConfig.APP_LEVEL_CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, appLevelKey, spec)

        val decryptedByteArray = cipher.doFinal(Base64.decode(digest, Base64.NO_WRAP))
        return String(decryptedByteArray)
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
        return keyGen.generateKey()
    }

}