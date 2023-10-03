package com.clonect.feeltalk.new_data.repository.account.dataSourceImpl

import com.clonect.feeltalk.new_data.repository.account.dataSource.AccountCacheDataSource
import com.clonect.feeltalk.new_domain.model.account.ConfigurationInfo
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.model.account.ServiceDataCountDto

class AccountCacheDataSourceImpl: AccountCacheDataSource {

    private var myInfo: MyInfo? = null
    private var configurationInfo: ConfigurationInfo? = null
    private var serviceDataCount: ServiceDataCountDto? = null

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

}