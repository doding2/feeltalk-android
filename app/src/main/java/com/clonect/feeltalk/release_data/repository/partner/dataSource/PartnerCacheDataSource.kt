package com.clonect.feeltalk.release_data.repository.partner.dataSource

import com.clonect.feeltalk.release_domain.model.partner.PartnerInfo

/**
 * Created by doding2 on 2023/09/27.
 */
interface PartnerCacheDataSource {
    fun savePartnerInfo(partnerInfo: PartnerInfo)
    fun getPartnerInfo(): PartnerInfo?
}