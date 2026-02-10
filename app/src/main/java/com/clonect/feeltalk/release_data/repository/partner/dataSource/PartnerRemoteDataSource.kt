package com.clonect.feeltalk.release_data.repository.partner.dataSource

import com.clonect.feeltalk.release_domain.model.partner.PartnerInfoDto

/**
 * Created by doding2 on 2023/09/27.
 */
interface PartnerRemoteDataSource {
    suspend fun getPartnerInfo(accessToken: String): PartnerInfoDto
}