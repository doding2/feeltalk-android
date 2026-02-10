package com.clonect.feeltalk.mvp_data.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.clonect.feeltalk.BuildConfig
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class DatabaseEncryptHelper {

    fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance(BuildConfig.APP_LEVEL_KEY_PROVIDER)
        keyStore.load(null)

        val secretKeyEntry: KeyStore.SecretKeyEntry? = keyStore.getEntry(BuildConfig.ROOM_DATABASE_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        val secretKey = secretKeyEntry?.secretKey

        return secretKey
            ?: generateAppLevelKey()
    }

    private fun generateAppLevelKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, BuildConfig.APP_LEVEL_KEY_PROVIDER)
        keyGen.init(
            KeyGenParameterSpec
                .Builder(
                    BuildConfig.ROOM_DATABASE_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )

        return keyGen.generateKey()
    }

}