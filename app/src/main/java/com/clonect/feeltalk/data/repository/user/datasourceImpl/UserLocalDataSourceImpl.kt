package com.clonect.feeltalk.data.repository.user.datasourceImpl

import android.content.Context
import com.clonect.feeltalk.data.db.FeeltalkDatabase
import com.clonect.feeltalk.data.repository.user.datasource.UserLocalDataSource
import com.clonect.feeltalk.data.utils.AppLevelEncryptHelper
import com.clonect.feeltalk.domain.model.data.user.UserInfo
import okio.use
import java.io.File

class UserLocalDataSourceImpl(
    private val context: Context,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
    private val feeltalkDatabase: FeeltalkDatabase,
): UserLocalDataSource {

    override suspend fun getAccessToken(): String? {
        val file = File(context.filesDir, "access_token.txt")
        if (!file.exists() || !file.canRead())
            return null
        val accessToken = file.bufferedReader().use {
            val encrypted = it.readLine()
            appLevelEncryptHelper.decrypt("accessToken", encrypted)
        }
        return accessToken
    }

    override suspend fun saveAccessToken(accessToken: String) {
        val file = File(context.filesDir, "access_token.txt")
        file.bufferedWriter().use {
            val encrypted = appLevelEncryptHelper.encrypt("accessToken", accessToken)
            it.write(encrypted)
        }
    }


    override suspend fun getUserInfo(): UserInfo? {
        val file = File(context.filesDir, "user_info.dat")
        if (!file.exists()) return null
        val dataBytes = file.readBytes()
        val decrypted = appLevelEncryptHelper.decryptObject<UserInfo>("userInfo", dataBytes)
        return decrypted as? UserInfo
    }

    override suspend fun saveUserInfo(userInfo: UserInfo) {
        val file = File(context.filesDir, "user_info.dat")
        val encrypted = appLevelEncryptHelper.encryptObject("userInfo", userInfo)
        file.writeBytes(encrypted)
    }


    override suspend fun getUserProfileUrl(): String? {
        val file = File(context.filesDir, "user_profile_url.txt")
        if (!file.exists()) return null
        val userProfileUrl = file.bufferedReader().use {
            val encrypted = it.readLine()
            appLevelEncryptHelper.decrypt("userProfileUrl", encrypted)
        }
        return userProfileUrl
    }

    override suspend fun saveUserProfileUrl(url: String) {
        val file = File(context.filesDir, "user_profile_url.txt")
        file.bufferedWriter().use {
            val encrypted = appLevelEncryptHelper.encrypt("userProfileUrl", url)
            it.write(encrypted)
        }
    }


    override suspend fun getCoupleRegistrationCode(): String? {
        val file = File(context.filesDir, "couple_registration_code.txt")
        if (!file.exists() || !file.canRead())
            return null
        val code = file.bufferedReader().use {
            val encrypted = it.readLine()
            appLevelEncryptHelper.decrypt("coupleRegistrationCode", encrypted)
        }
        return code
    }

    override suspend fun saveCoupleRegistrationCode(code: String) {
        val file = File(context.filesDir, "couple_registration_code.txt")
        file.bufferedWriter().use {
            val encrypted = appLevelEncryptHelper.encrypt("coupleRegistrationCode", code)
            it.write(encrypted)
        }
    }

    override suspend fun removeCoupleRegistrationCode() {
        val file = File(context.filesDir, "couple_registration_code.txt")
        file.delete()
    }


    override suspend fun getGoogleIdToken(): String? {
        val file = File(context.filesDir, "google_id_token.txt")
        if (!file.exists())
            return null
        val idToken = file.bufferedReader().use {
            val encrypted = it.readLine()
            appLevelEncryptHelper.decrypt("googleIdToken", encrypted)
        }
        return idToken
    }

    override suspend fun saveGoogleIdToken(idToken: String) {
        val file = File(context.filesDir, "google_id_token.txt")
        file.bufferedWriter().use {
            val encrypted = appLevelEncryptHelper.encrypt("googleIdToken", idToken)
            it.write(encrypted)
        }
    }

    override suspend fun getAppleLoggedIn(): Boolean? {
        val file = File(context.filesDir, "is_apple_logged_in.txt")
        if (!file.exists())
            return null
        val isLoggedIn = file.bufferedReader().use {
            val encrypted = it.readLine()
            appLevelEncryptHelper.decrypt("isAppleLoggedIn", encrypted)
        }
        return isLoggedIn?.toBooleanStrictOrNull()
    }

    override suspend fun saveIsAppleLoggedIn(isLoggedIn: Boolean) {
        val file = File(context.filesDir, "is_apple_logged_in.txt")
        file.bufferedWriter().use {
            val encrypted = appLevelEncryptHelper.encrypt("isAppleLoggedIn", isLoggedIn.toString())
            it.write(encrypted)
        }
    }


    override suspend fun clearCoupleInfo(): Boolean {
        val registrationCodeFile = File(context.filesDir, "couple_registration_code.txt")
        val coupleAnniversaryFile = File(context.filesDir, "couple_anniversary.txt")
        val userInfoFile = File(context.filesDir, "user_info.dat")

        feeltalkDatabase.clearAllTables()
        context.deleteDatabase("feeltalkDatabase.db")

        return registrationCodeFile.delete()
                && coupleAnniversaryFile.delete()
                && userInfoFile.delete()
    }

    override suspend fun clearAllExceptKeys(): Boolean {
        val idTokenFile = File(context.filesDir, "google_id_token.txt")
        val isAppleLoggedInFile = File(context.filesDir, "is_apple_logged_in.txt")
        val accessTokenFile = File(context.filesDir, "access_token.txt")
        val registrationCodeFile = File(context.filesDir, "couple_registration_code.txt")
        val userInfoFile = File(context.filesDir, "user_info.dat")

        feeltalkDatabase.clearAllTables()
        context.deleteDatabase("feeltalkDatabase.db")

        return idTokenFile.delete()
                && isAppleLoggedInFile.delete()
                && accessTokenFile.delete()
                && registrationCodeFile.delete()
                && userInfoFile.delete()
    }
}