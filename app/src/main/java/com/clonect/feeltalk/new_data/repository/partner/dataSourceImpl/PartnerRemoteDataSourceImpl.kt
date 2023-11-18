package com.clonect.feeltalk.new_data.repository.partner.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.common.FeelTalkException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.partner.dataSource.PartnerRemoteDataSource
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfoDto

/**
 * Created by doding2 on 2023/09/27.
 */
class PartnerRemoteDataSourceImpl(
    private val clonectService: ClonectService
): PartnerRemoteDataSource {
    override suspend fun getPartnerInfo(accessToken: String): PartnerInfoDto {
        val response = clonectService.getPartnerInfo(accessToken)
        if (!response.isSuccessful) throw FeelTalkException.ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }
}