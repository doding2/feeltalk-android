package com.clonect.feeltalk.release_data.repository.partner.dataSourceImpl

import com.clonect.feeltalk.release_data.repository.partner.dataSource.PartnerCacheDataSource
import com.clonect.feeltalk.release_domain.model.partner.PartnerInfo

/**
 * Created by doding2 on 2023/09/27.
 */
class PartnerCacheDataSourceImpl: PartnerCacheDataSource {

    private var partnerInfo: PartnerInfo? = null

    override fun savePartnerInfo(partnerInfo: PartnerInfo) {
        this.partnerInfo = partnerInfo
    }

    override fun getPartnerInfo(): PartnerInfo? = partnerInfo

}