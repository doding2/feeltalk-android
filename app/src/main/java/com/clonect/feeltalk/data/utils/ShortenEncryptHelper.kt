package com.clonect.feeltalk.data.utils

import android.util.Base64
import com.clonect.feeltalk.BuildConfig
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ShortenEncryptHelper {

    fun encrypt(message: String): String {
        val textBytes = message.toByteArray(Charsets.UTF_8)
        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(BuildConfig.SHORTEN_ENCRYPT_HELPER_IV.encodeToByteArray())
        val newKey = SecretKeySpec(BuildConfig.SHORTEN_ENCRYPT_HELPER_KEY.encodeToByteArray(), "AES")
        val cipher: Cipher = Cipher.getInstance(BuildConfig.SHORTEN_ENCRYPT_HELPER_CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec)
        return Base64.encodeToString(cipher.doFinal(textBytes), Base64.NO_WRAP)
    }

    fun decrypt(digest: String): String {
        val decodedByte: ByteArray = Base64.decode(digest, Base64.NO_WRAP)
        val iv = IvParameterSpec(BuildConfig.SHORTEN_ENCRYPT_HELPER_IV.toByteArray())
        val keySpec = SecretKeySpec(BuildConfig.SHORTEN_ENCRYPT_HELPER_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance(BuildConfig.SHORTEN_ENCRYPT_HELPER_CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
        val output = cipher.doFinal(decodedByte)
        return String(output)
    }
}