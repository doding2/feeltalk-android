package com.clonect.feeltalk.new_data.repository.account.dataSourceImpl

import android.content.Context
import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountLocalDataSource
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.new_domain.model.account.LockQA
import com.clonect.feeltalk.new_domain.model.token.TokenInfo
import java.io.File

class AccountLocalDataSourceImpl(
    private val context: Context,
    private val appLevelEncryptHelper: AppLevelEncryptHelper
): AccountLocalDataSource {

    override suspend fun saveLockPassword(password: String) {
        val file = File(context.filesDir, "lockPassword.dat")
        val encrypted = appLevelEncryptHelper.encryptObject("lockPassword", password)
        file.writeBytes(encrypted)
    }

    override suspend fun saveLockQA(lockQA: LockQA) {
        val file = File(context.filesDir, "lockQA.dat")
        val encrypted = appLevelEncryptHelper.encryptObject("lockQA", lockQA)
        file.writeBytes(encrypted)
    }

    override suspend fun getLockPassword(): String? {
        val file = File(context.filesDir, "lockPassword.dat")
        if (!file.exists() || !file.canRead()) return null
        val dataBytes = file.readBytes()
        val decrypted = appLevelEncryptHelper.decryptObject<TokenInfo>("lockPassword", dataBytes)
        return decrypted as? String
    }

    override suspend fun getLockQA(): LockQA? {
        val file = File(context.filesDir, "lockQA.dat")
        if (!file.exists() || !file.canRead()) return null
        val dateBytes = file.readBytes()
        val encrypted = appLevelEncryptHelper.decryptObject<TokenInfo>("lockQA", dateBytes)
        return  encrypted as? LockQA
    }

    override suspend fun checkLockPassword(): Boolean {
        val file = File(context.filesDir, "lockPassword.dat")
        return file.exists() && file.canRead()
    }

    override suspend fun deleteLockInfo(): Boolean {
        val file = File(context.filesDir, "lockPassword.dat")
        val qaFile = File(context.filesDir, "lockQA.dat")
        return file.delete() && qaFile.delete()
    }

}