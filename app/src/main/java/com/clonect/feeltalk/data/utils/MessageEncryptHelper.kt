package com.clonect.feeltalk.data.utils

import com.clonect.feeltalk.BuildConfig
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MessageEncryptHelper {

    fun encryptAES(key: ByteArray, message: String): ByteArray {
        val textBytes = message.toByteArray()
        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(ByteArray(16))
        val newKey = SecretKeySpec(key, "AES")
        val cipher: Cipher = Cipher.getInstance(BuildConfig.SHORTEN_ENCRYPT_HELPER_CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec)
        return cipher.doFinal(textBytes)
    }

    fun decryptAES(key: ByteArray, digest: ByteArray): String {
        val iv = IvParameterSpec(ByteArray(16))
        val keySpec = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance(BuildConfig.SHORTEN_ENCRYPT_HELPER_CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
        val output = cipher.doFinal(digest)
        return String(output)
    }
}