package com.clonect.feeltalk.release_data.repository.challenge.dataSourceImpl

import android.content.Context
import com.clonect.feeltalk.release_data.repository.challenge.dataSource.ChallengeLocalDataSource
import com.clonect.feeltalk.release_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.release_domain.model.token.TokenInfo
import java.io.File

class ChallengeLocalDataSourceImpl(
    private val context: Context,
    private val appLevelEncryptHelper: AppLevelEncryptHelper
): ChallengeLocalDataSource {
    override suspend fun setChallengeUpdated(isUpdated: Boolean) {
        val file = File(context.filesDir, "isChallengeUpdated.dat")
        val encrypted = appLevelEncryptHelper.encryptObject("isChallengeUpdated", isUpdated)
        file.writeBytes(encrypted)
    }

    override suspend fun getChallengeUpdated(): Boolean {
        val file = File(context.filesDir, "isChallengeUpdated.dat")
        if (!file.exists() || !file.canRead()) return false
        val dataBytes = file.readBytes()
        val decrypted = appLevelEncryptHelper.decryptObject<TokenInfo>("isChallengeUpdated", dataBytes)
        return decrypted as? Boolean ?: false
    }
}