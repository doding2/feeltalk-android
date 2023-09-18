package com.clonect.feeltalk.new_data.repository.account.dataSource

import com.clonect.feeltalk.new_domain.model.account.LockQA

interface AccountLocalDataSource {

    suspend fun saveLockPassword(password: String)
    suspend fun saveLockQA(lockQA: LockQA)
    suspend fun getLockPassword(): String?
    suspend fun getLockQA(): LockQA?
    suspend fun checkLockPassword(): Boolean
    suspend fun deleteLockInfo(): Boolean

}