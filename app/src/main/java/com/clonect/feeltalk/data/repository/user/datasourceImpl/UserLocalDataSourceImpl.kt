package com.clonect.feeltalk.data.repository.user.datasourceImpl

import android.content.Context
import android.os.Build
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.use
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserLocalDataSourceImpl(private val context: Context): UserLocalDataSource {

    override suspend fun getCoupleRegistrationCode(): String? = suspendCoroutine { continuation ->
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.dataDir
        } else {
            context.cacheDir
        }
        val file = File(dir, "couple_registration_code.txt")
        file.bufferedReader().use {
            continuation.resume(it.readLine())
        }
    }

    override suspend fun saveCoupleRegistrationCode(code: String) {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.dataDir
        } else {
            context.cacheDir
        }
        val file = File(dir, "couple_registration_code.txt")
        file.bufferedWriter().use {
            it.write(code)
        }
    }

    override suspend fun removeCoupleRegistrationCode() {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.dataDir
        } else {
            context.cacheDir
        }
        val file = File(dir, "couple_registration_code.txt")
        file.delete()
    }

}