package com.clonect.feeltalk.new_data.repository.question.dataSourceImpl

import android.content.Context
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionLocalDataSource
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import java.io.File

class QuestionLocalDataSourceImpl(
    private val context: Context,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
): QuestionLocalDataSource {

    override fun setQuestionUpdated(isUpdated: Boolean) {
        val file = File(context.filesDir, "isQuestionUpdated.dat")
        val encrypted = appLevelEncryptHelper.encryptObject("isQuestionUpdated", isUpdated)
        file.writeBytes(encrypted)
    }

    override fun getQuestionUpdated(): Boolean {
        val file = File(context.filesDir, "isQuestionUpdated.dat")
        if (!file.exists() || !file.canRead()) return false
        val dataBytes = file.readBytes()
        val decrypted = appLevelEncryptHelper.decryptObject<TokenInfo>("isQuestionUpdated", dataBytes)
        return decrypted as? Boolean ?: false
    }
}