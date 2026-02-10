package com.clonect.feeltalk.release_data.repository.account.dataSource

import com.clonect.feeltalk.release_domain.model.account.ConfigurationInfo
import com.clonect.feeltalk.release_domain.model.account.LockQA
import com.clonect.feeltalk.release_domain.model.account.MyInfo

interface AccountLocalDataSource {

    suspend fun saveMyInfo(myInfo: MyInfo)
    suspend fun getMyInfo(): MyInfo?

    suspend fun saveConfigurationInfo(configurationInfo: ConfigurationInfo)
    suspend fun getConfigurationInfo(): ConfigurationInfo?

    suspend fun saveLockPassword(password: String)
    suspend fun saveLockQA(lockQA: LockQA)
    suspend fun getLockPassword(): String?
    suspend fun getLockQA(): LockQA?
    suspend fun checkLockPassword(): Boolean
    suspend fun deleteLockInfo(): Boolean

    suspend fun clearInternalStorage()
}