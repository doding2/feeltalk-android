package com.clonect.feeltalk.data.repository.user.datasourceImpl

import android.content.Context
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.domain.model.user.AccessToken
import okio.use
import java.io.File

class UserLocalDataSourceImpl(
    private val context: Context
): UserLocalDataSource {

    override suspend fun getAccessToken(): AccessToken? {
        val file = File(context.filesDir, "access_token.txt")
        if (!file.exists() || !file.canRead())
            return null
        val accessTokenString = file.bufferedReader().use {
            it.readLine()
        }
        return AccessToken(accessTokenString)
    }

    override suspend fun saveAccessToken(accessToken: AccessToken) {
        val file = File(context.filesDir, "access_token.txt")
        file.bufferedWriter().use {
            it.write(accessToken.value)
        }
    }


    override suspend fun getCoupleRegistrationCode(): String? {
        val file = File(context.filesDir, "couple_registration_code.txt")
        if (!file.exists() || !file.canRead())
            return null
        val code = file.bufferedReader().use {
            it.readLine()
        }
        return code
    }

    override suspend fun saveCoupleRegistrationCode(code: String) {
        val file = File(context.filesDir, "couple_registration_code.txt")
        file.bufferedWriter().use {
            it.write(code)
        }
    }

    override suspend fun removeCoupleRegistrationCode() {
        val file = File(context.filesDir, "couple_registration_code.txt")
        file.delete()
    }

}