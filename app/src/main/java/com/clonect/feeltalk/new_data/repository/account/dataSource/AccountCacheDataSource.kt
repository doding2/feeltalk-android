package com.clonect.feeltalk.new_data.repository.account.dataSource

import com.clonect.feeltalk.new_domain.model.account.ConfigurationInfo
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.model.account.ServiceDataCountDto
import kotlinx.coroutines.flow.Flow

interface AccountCacheDataSource {

    fun saveMyInfo(myInfo: MyInfo)
    fun getMyInfo(): MyInfo?

    fun saveConfigurationInfo(configurationInfo: ConfigurationInfo)
    fun getConfigurationInfo(): ConfigurationInfo?

    fun saveServiceDataCount(serviceDataCountDto: ServiceDataCountDto)
    fun getServiceDataCount(): ServiceDataCountDto?

    suspend fun setCoupleCreated(isCreated: Boolean)
    suspend fun getCoupleCreatedFlow(): Flow<Boolean>
}