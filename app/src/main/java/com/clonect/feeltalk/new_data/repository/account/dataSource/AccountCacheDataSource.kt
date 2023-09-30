package com.clonect.feeltalk.new_data.repository.account.dataSource

import com.clonect.feeltalk.new_domain.model.account.ConfigurationInfo
import com.clonect.feeltalk.new_domain.model.account.MyInfo

interface AccountCacheDataSource {

    fun saveMyInfo(myInfo: MyInfo)
    fun getMyInfo(): MyInfo?

    fun saveConfigurationInfo(configurationInfo: ConfigurationInfo)
    fun getConfigurationInfo(): ConfigurationInfo?
}