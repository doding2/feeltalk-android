package com.clonect.feeltalk.release_data.repository.token.dataSourceImpl

import android.content.Context
import com.clonect.feeltalk.release_data.repository.token.dataSource.TokenLocalDataSource
import com.clonect.feeltalk.release_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.release_domain.model.token.TokenInfo
import java.io.File

class TokenLocalDataSourceImpl(
    private val context: Context,
    private val appLevelEncryptHelper: AppLevelEncryptHelper
): TokenLocalDataSource {

    override fun saveTokenInfo(tokenInfo: TokenInfo) {
        val file = File(context.filesDir, "tokenInfo.dat")
        val encrypted = appLevelEncryptHelper.encryptObject("tokenInfo", tokenInfo)
        file.writeBytes(encrypted)
    }

    override fun getTokenInfo(): TokenInfo? {
        val file = File(context.filesDir, "tokenInfo.dat")
        if (!file.exists() || !file.canRead()) return null
        val dataBytes = file.readBytes()
        val decrypted = appLevelEncryptHelper.decryptObject<TokenInfo>("tokenInfo", dataBytes)
        return decrypted as? TokenInfo
    }

    override fun deleteAll(): Boolean {
        val tokenInfo = File(context.filesDir, "tokenInfo.dat")
        return tokenInfo.delete()
    }
}