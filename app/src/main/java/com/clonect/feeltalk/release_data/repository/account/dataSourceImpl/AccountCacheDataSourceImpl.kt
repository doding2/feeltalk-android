package com.clonect.feeltalk.release_data.repository.account.dataSourceImpl

import com.clonect.feeltalk.release_data.repository.account.dataSource.AccountCacheDataSource
import com.clonect.feeltalk.release_domain.model.account.ConfigurationInfo
import com.clonect.feeltalk.release_domain.model.account.MyInfo
import com.clonect.feeltalk.release_domain.model.account.ServiceDataCountDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountCacheDataSourceImpl: AccountCacheDataSource {

    private var myInfo: MyInfo? = null
    private var configurationInfo: ConfigurationInfo? = null
    private var serviceDataCount: ServiceDataCountDto? = null
    private var isCoupleCreated = MutableStateFlow(false)

    override fun saveMyInfo(myInfo: MyInfo) {
        this.myInfo = myInfo
    }
    override fun getMyInfo(): MyInfo? = myInfo


    override fun saveConfigurationInfo(configurationInfo: ConfigurationInfo) {
        this.configurationInfo = configurationInfo
    }
    override fun getConfigurationInfo(): ConfigurationInfo? = configurationInfo

    override fun saveServiceDataCount(serviceDataCountDto: ServiceDataCountDto) {
        this.serviceDataCount = serviceDataCountDto
    }
    override fun getServiceDataCount(): ServiceDataCountDto? = serviceDataCount

    override suspend fun setCoupleCreated(isCreated: Boolean) {
        isCoupleCreated.value = isCreated
    }

    override suspend fun getCoupleCreatedFlow(): Flow<Boolean> {
        isCoupleCreated.value = false
        return isCoupleCreated.asStateFlow()
    }

}