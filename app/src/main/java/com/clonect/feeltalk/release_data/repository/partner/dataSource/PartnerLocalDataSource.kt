package com.clonect.feeltalk.release_data.repository.partner.dataSource

import com.clonect.feeltalk.release_domain.model.partner.PartnerInfo

/**
 * Created by doding2 on 2023/09/27.
 */
interface PartnerLocalDataSource {
    suspend fun savePartnerInfo(partnerInfo: PartnerInfo)
    suspend fun getPartnerInfo(): PartnerInfo?
}