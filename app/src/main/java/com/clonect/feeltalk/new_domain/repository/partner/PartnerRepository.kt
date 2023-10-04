package com.clonect.feeltalk.new_domain.repository.partner

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import kotlinx.coroutines.flow.Flow

/**
 * Created by doding2 on 2023/09/27.
 */
interface PartnerRepository {
    suspend fun getPartnerInfo(accessToken: String): Resource<PartnerInfo>
    suspend fun getPartnerInfoFlow(accessToken: String): Flow<Resource<PartnerInfo>>
}